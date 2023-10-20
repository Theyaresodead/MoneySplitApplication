package com.example.tipapplication

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tipapplication.components.inputFields
import com.example.tipapplication.ui.theme.TipApplicationTheme
import com.example.tipapplication.util.CalculateTotalTip
import com.example.tipapplication.util.calculateTotalPerson
import com.example.tipapplication.widgets.RoundIconButton
import org.w3c.dom.Text

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
          MyApp {
              TipCalculator()
          }
        }
    }
}

@Composable
fun MyApp(content: @Composable ()->Unit)
{
    TipApplicationTheme {
        // A surface container using the 'background' color from the theme
        Surface(color = Color.White) {
            content()
        }
    }
}
@ExperimentalComposeUiApi
@Composable
fun TipCalculator() {
    Surface(modifier = Modifier.padding(12.dp), color = Color.White) {
        Column() {
            MainContent()
        }
    }
}

//@Preview
@Composable
fun TopHeader(totalperson:Double=0.0){
    val total="%.2f".format(totalperson)
    androidx.compose.material.Surface(modifier = Modifier
        .fillMaxWidth()
        .height(150.dp)
        .padding(12.dp)
        .clip(shape = CircleShape.copy(all = CornerSize(12.dp))),
        color= Color(0xFFE9D7F7)
    ) {
        Column(verticalArrangement = Arrangement.Center
            ,horizontalAlignment = Alignment.CenterHorizontally) {
             Text(text = "Total Bill per Person", style = MaterialTheme.typography.subtitle1)
            Text(text = "\$$total", style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.ExtraBold)
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Preview
@Composable
fun MainContent()
{
    val click= remember {
        mutableStateOf(1)
    }
    val range =IntRange(start=1 , endInclusive = 100)
    val tipAmountSale= remember {
        mutableStateOf(0.0)
    }
    val totalPerPersonState= remember {
        mutableStateOf(0.0)
    }
    Column(modifier = Modifier.padding(all=12.dp)) {
    BillForm(modifier = Modifier, range = range,click=click, tipAmountState =tipAmountSale,
    totalPerPersonState=totalPerPersonState){
    }
}

}

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BillForm(modifier: Modifier=Modifier,
             range: IntRange=1..100,
             click :MutableState<Int>,tipAmountState: MutableState<Double>,
             totalPerPersonState: MutableState<Double>,
              onValueChange:(String) -> Unit={})
{
    val totalBill= remember {
        mutableStateOf("")
    }
    val validState= remember(totalBill.value) {
        totalBill.value.trim().isNotEmpty()
    }
    val sliderPositionState = remember {
        mutableStateOf(0f)
    }
    val tippercentage= (sliderPositionState.value * 100 ).toInt()

    TopHeader(totalperson = totalPerPersonState.value)


    val keyboardController= LocalSoftwareKeyboardController.current
    androidx.compose.material.Surface(
        modifier = Modifier
            .padding(2.dp)
            .fillMaxWidth()
            .clip(shape = CircleShape.copy(all = CornerSize(8.dp))),
            color= Color.LightGray,
        border = BorderStroke(width=1.dp , color= Color.LightGray)
    ) {
        Column(modifier = Modifier.padding(6.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start) {
            inputFields(valueState = totalBill, lableId ="Enter Bill" ,
                enabled = true, isSingleLine =true ,
                onAction = KeyboardActions{
                     if(!validState) return@KeyboardActions
                      onValueChange(totalBill.value.trim())

                    keyboardController?.hide()
                })
           if(validState) {
               Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.padding(2.dp))
               {
                  Text(text = "Split",modifier=Modifier.align(alignment = Alignment.CenterVertically))
                   Spacer(modifier = Modifier.width(120.dp))
                   Row(modifier = Modifier.padding(horizontal = 3.dp),
                    horizontalArrangement = Arrangement.End)
                   {
                      RoundIconButton( imageVector = Icons.Default.Remove , onClick = {
                            click.value=
                                if(click.value>1) click.value-1
                          else 1
                          totalPerPersonState.value =
                              calculateTotalPerson(totalBill = totalBill.value.toDouble(), splitBy = click.value,
                                  tippercentage=tippercentage)

                      })
                       Text(text = "${click.value}",
                           modifier = Modifier
                               .align(Alignment.CenterVertically)
                               .padding(start = 9.dp, end = 9.dp)
                       )
                       RoundIconButton( imageVector = Icons.Default.Add , onClick = {
                           if(click.value < range.last){
                               click.value=click.value+1
                               totalPerPersonState.value =
                                   calculateTotalPerson(totalBill = totalBill.value.toDouble(), splitBy = click.value,
                                       tippercentage=tippercentage)
                           }
                       })
                   }
               }
            // Tip Row
            Row(modifier = Modifier.padding(horizontal = 3.dp, vertical = 12.dp)){
                Text(text = "Tip",
                modifier = Modifier.align(Alignment.CenterVertically))
                Spacer(modifier = Modifier.width(200.dp))
                Text(text = "$ ${tipAmountState.value}",
                    modifier = Modifier.align(Alignment.CenterVertically))
            }
             Column(verticalArrangement = Arrangement.Center,
             horizontalAlignment = Alignment.CenterHorizontally){
                Text(text = "$tippercentage%")
                 Spacer(modifier = Modifier.height(14.dp))
                 //Slider 

                 Slider(value = sliderPositionState.value, onValueChange = {
                     newValue -> sliderPositionState.value=newValue
                     tipAmountState.value =
                         CalculateTotalTip(totalBill = totalBill.value.toDouble(),
                             tippercentage=tippercentage)
                     totalPerPersonState.value =
                         calculateTotalPerson(totalBill = totalBill.value.toDouble(), splitBy = click.value,
                         tippercentage=tippercentage)

                 }, modifier = Modifier.padding(start=16.dp,end=16.dp), steps = 10)
             }
           }
            else
           {
               Box(){}
           }
        }
    }
}




@OptIn(ExperimentalComposeUiApi::class)
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TipApplicationTheme {
      MyApp {
          TipCalculator()
      }
    }
}

