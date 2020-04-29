package com.example.moonphases

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.Math.floor
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.ceil
import kotlin.math.pow
import kotlin.math.sin

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnFulls.setOnClickListener {
            val intencja = Intent(this,SecondActivity::class.java)

            startActivity(intencja)
        }

        btnSettings.setOnClickListener {
            val intent = Intent(this,ThirdActivity::class.java)
            startActivity(intent)
        }

        textTodaysPhase.setText("Dzisiaj: "+currentPhase().toString()+"%")

        textLastNewMoon.setText(lastNewMoon())
        textNextFullMoon.setText(nextFullMoon())

    }

    override fun onRestart() {
        super.onRestart()
        textTodaysPhase.setText("Dzisiaj: "+currentPhase().toString()+"%")
        textLastNewMoon.setText(lastNewMoon())
        textNextFullMoon.setText(nextFullMoon())
    }

    fun lastNewMoon():String{
        var lp = 2551442.98 //num of seconds in lunar period

        var data1 = LocalDateTime.of(2019,12,26,5,13) // new moon in 2019
        var data2 = LocalDateTime.now()
        var days_between = ChronoUnit.DAYS.between(data1,data2)
        var num_of_newmoons:Int = ((days_between*24.0*3600.0)/lp).toInt()
        var data_lastNewMoon = data1.plusSeconds(((num_of_newmoons)*lp).toLong())
        var return_date = data_lastNewMoon.toLocalDate().toString()
        return "Poprzedni Nów: " + return_date
    }

    fun nextFullMoon():String{
        var lp = 2551442.98 //num of seconds in lunar period

        var data1 = LocalDateTime.of(2019,12,12,5,14) // full moon in 2019
        var data2 = LocalDateTime.now()
        var days_between = ChronoUnit.DAYS.between(data1,data2)
        var num_of_fullmoons:Int = ((days_between*24.0*3600.0)/lp).toInt()
        var data_nextFullMoon = data1.plusSeconds(((num_of_fullmoons+1)*lp).toLong())
        var return_date = data_nextFullMoon.toLocalDate().toString()
        return "Następna Pełnia: " + return_date
    }

    //Odczyt dzisiejszej fazy
    fun currentPhase():Double{
        var todaysDate = LocalDate.now()
        var todaysYear = todaysDate.year.toInt()
        var todaysMonth = todaysDate.monthValue.toInt()
        var todaysDay = todaysDate.dayOfMonth.toInt()
        var outcomeNumber:Int = 0
        var algorytm_direction = load()
        var algorytm = algorytm_direction[0]
        var direction = algorytm_direction[1]
        if(algorytm=="Simple") {outcomeNumber = simple(todaysYear,todaysMonth,todaysDay)}
        else if(algorytm=="Conway") {outcomeNumber = conway(todaysYear.toDouble(),todaysMonth,todaysDay)}
        else if(algorytm=="Trig2") {outcomeNumber = trig2(todaysYear,todaysMonth,todaysDay)}
        else if(algorytm=="Trig1") {outcomeNumber = trig1(todaysYear,todaysMonth,todaysDay)}
        Toast.makeText(this,"Użyty algorytm: " + algorytm + "\nObserowane z: " + direction,Toast.LENGTH_SHORT).show()

        if(direction == "North") {direction="n"}
        else{direction = "s"}
        var outcomePercentage:Double = String.format("%.2f",(outcomeNumber.toDouble()/(29.toDouble()))*100.0).toDouble()

        var imgName = direction + outcomeNumber.toString()

        var imgId = resources.getIdentifier(imgName,"mipmap",packageName)
        imgMoon.setImageResource(imgId)

        return outcomePercentage
    }

    //ODCZYT USTAWIEN Z PLIKU
    fun load():Array<String> {
        var algorytm = "Simple"
        var direction = "South"
        try {
            val filename = "ustawieniaFazy.txt"
            val file = InputStreamReader(openFileInput(filename))
            val br = BufferedReader(file)
            algorytm = br.readLine().toString()
            direction = br.readLine().toString()
            file.close()
        }
        catch(e: Exception){

        }
        return arrayOf(algorytm,direction)
    }




    //DIFFERENT ALGORITHMS FOR MOON PHASE CALCULATION
    fun conway(year: Double,month: Int,day: Int): Int{
        var r = year%100
        r%=19
        if(r>9) {r-=19}
        r = ((r*11)%30) + month + day
        if(month<3) {r+=2}
       if(year<2000){
           r -= 4
       }
        else{
           r-=8.3
       }
        r = floor(r+0.5)%30
        if(r<0) {
            r+=30
        }
        return r.toInt()
    }


    fun simple(year:Int, month:Int, day:Int): Int{
        var lp = 2551443
        var date_given = LocalDate.of(year, month, day)
        var new_moon = LocalDate.of(1900, 1, 1)
        var phase = (ChronoUnit.DAYS.between(new_moon,date_given)*24*3600)%lp
        var phase_long = floor((phase/(3600*24)).toDouble())
        return phase_long.toInt()

    }

    fun julday(year_given:Int,month:Int,day:Int):Int {
        var year = year_given
        if(year<0) {year+=1}
        var jy = year
        var jm = month+1
        if(month<=2) {
            jy-=1
            jm+=12
        }
        var jul = floor(365.25 * jy.toDouble()) + floor(30.6001 * jm.toDouble()) + day + 1720995
        if((day+31*(month+12*year)) >= (15+31*(10+12*1582))) {
            var ja = floor(0.01*jy.toDouble())
            jul = jul + 2 - ja + floor(0.25*ja.toDouble())
        }
        return jul.toInt()
    }

    fun trig2(year:Int,month:Int,day:Int):Int {
        var n = floor(12.37*(year-1900+((1.0*month-0.5)/12.0)))
        val RAD = 3.14159265/180.0
        var t = n/1236.85
        var t2 = t*t
        var aS = 359.2242 + 29.105356 * n
        var am = 306.0253 + 385.816918 * n + 0.010730 * t2
        var xtra = 0.75933 + 1.53058868 *n + ((1.178.pow(-4)) - (1.55.pow(-7))*t)*t2
        xtra+= (0.1734 - (3.93.pow(-4))*t)* sin(RAD*aS) - 0.4068 * sin(RAD*am)
        var i:Double
        if(xtra>0.0) {i = floor(xtra)}
        else {i = ceil(xtra-1.0)}
        var jl = julday(year,month,day)
        var jd = (2415020+28*n) + i
        return ((jl-jd+30)%30).toInt()
    }

    fun GetFrac(fr:Double):Double {
        return (fr - floor(fr))
    }

    fun trig1(year:Int,month:Int,day:Int):Int {
        var thisJD = julday(year,month,day)
        var degToRad = 3.14159265 / 180.0
        var K0 = floor((year-1900)*12.3685)
        var T = (year.toDouble()-1899.5)/100.0
        var T2 = T*T
        var T3 = T*T*T
        var J0 = 2415020 + 29 * K0.toInt()
        var F0 = 0.0001178*T2 - 0.000000155*T3 + (0.75933 + 0.53058868*K0) - (0.000837*T + 0.000335*T2)
        var M0 = 360 * (GetFrac(K0*0.08084821133)) + 359.2242 - 0.0000333*T2 - 0.00000347*T3
        var M1 = 360*(GetFrac(K0*0.07171366128)) + 306.0253 + 0.0107306*T2 + 0.00001236*T3
        var B1 = 360*(GetFrac(K0*0.08519585128)) + 21.2964 - (0.0016528*T2) - (0.00000239*T3)
        var phase = 0
        var jday = 0.0
        var oldJ:Double = 0.0
        while(jday<thisJD) {
            var F = F0 + 1.530588*phase
            var M5 = (M0 + phase*29.10535608)*degToRad
            var M6 = (M1 + phase*385.81691806)*degToRad
            var B6 = (B1 + phase*390.67050646) * degToRad
            F -= 0.4068*sin(M6) + (0.1734 - 0.000393*T)*sin(M5)
            F+= 0.0161*sin(2*M6) + 0.0104*sin(2*B6)
            F-= 0.0074 * sin(M5-M6) - 0.0051*sin(M5+M6)
            F+= 0.0021*sin(2*M5) + 0.0010*sin(2*B6-M6)
            F+= 0.5/1440
            oldJ = jday.toDouble()
            jday = J0 + 28*phase + floor(F)
            phase+=1
        }
        return ((thisJD-oldJ)%30).toInt()
    }




}
