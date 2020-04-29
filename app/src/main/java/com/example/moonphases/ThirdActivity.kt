package com.example.moonphases

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_third.*
import java.io.OutputStreamWriter
import java.lang.Exception

class ThirdActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)

        var optionsAlgorithm = arrayOf("Simple","Conway","Trig1","Trig2")
        var optionsDirection = arrayOf("South","North")

        spinnerAlgorithm.adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,optionsAlgorithm)
        spinnerDirection.adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,optionsDirection)
    }

    //ZAPIS DO PLIKU ALGORYTMU I POLKULI
    fun save(v:View) {
        try{
            val filename = "ustawieniaFazy.txt"
            val file = OutputStreamWriter(openFileOutput(filename, Context.MODE_PRIVATE))
            var notes:String = spinnerAlgorithm.selectedItem.toString() +"\n"+spinnerDirection.selectedItem.toString()
            file.write(notes)
            file.flush()
            file.close()
            Toast.makeText(this,"Zapis ustawień się powiódł!", Toast.LENGTH_SHORT).show()
        }
        catch (e: Exception) {
            Toast.makeText(this,"Zapis ustawień się nie powiódł!", Toast.LENGTH_SHORT).show()
        }
    }

}