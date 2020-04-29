package com.example.moonphases

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_second.*
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class SecondActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        var year_chosen = enterYear.text.toString().toInt()

        show_dates(year_chosen)


        btnIncrement.setOnClickListener {
            year_chosen = enterYear.text.toString().toInt()
            year_chosen = year_chosen + 1
            enterYear.setText(year_chosen.toString())
        }

        btnDecrement.setOnClickListener {
            year_chosen = enterYear.text.toString().toInt()
            year_chosen = year_chosen - 1
            enterYear.setText(year_chosen.toString())
        }

        enterYear.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {

                var year_chosen = s.toString()
                if(year_chosen.length==4 && year_chosen[0].toString()!="0" && year_chosen.toInt()<1900){
                    textAllDates.setText("Proszę podać rok po 1899")
                }
                else if(year_chosen.length==4 && year_chosen[0].toString()!="0") {

                    show_dates(year_chosen.toInt())
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
    }

    fun full_moons(year_given:Int):List<LocalDateTime> {
        var lp = 2551442.98 //num of seconds in lunar period

        var data1 = LocalDateTime.of(1899,12,17,1,31) //last full moon before 1900s
        var data2 = LocalDateTime.of(year_given-1,12,31,23,59)
        var days_between = ChronoUnit.DAYS.between(data1,data2)
        var num_of_halfmoons:Int = ((days_between*24.0*3600.0)/lp).toInt()



        var data_firsthalfmoon = data1.plusSeconds((num_of_halfmoons+1)*lp.toLong())
        var year_return = data_firsthalfmoon.year
        if(year_return!=year_given) {
            data_firsthalfmoon = data_firsthalfmoon.plusSeconds(lp.toLong())
            year_return = data_firsthalfmoon.year

        }

        val daty = mutableListOf(data_firsthalfmoon)
        daty.removeAt(0)
        while(year_return == year_given) {
            daty.add(data_firsthalfmoon)
            data_firsthalfmoon = data_firsthalfmoon.plusSeconds(lp.toLong())
            year_return = data_firsthalfmoon.year
        }
        return daty
    }

    fun show_dates(year_chosen:Int){
        var all_dates:String = ""
        var return_list = full_moons(year_chosen)
        for(date in return_list) {
            all_dates = all_dates + date.toLocalDate().toString() + "\n"
        }
        textAllDates.setText(all_dates)

    }
}