package com.xabif.arboretum

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.concurrent.thread
import androidx.core.net.toUri

class MainActivity : AppCompatActivity() {
    private lateinit var map: MapContent;
    private lateinit var info_label: TextView;
    private lateinit var query_text: EditText;
    private lateinit var tree_list: ListView;
    private lateinit var cur_trees: List<Tree>;

    private fun loadByQuery(q: String) {
        this@MainActivity.cur_trees = this@MainActivity.map.lookup(q);
        val texts: MutableList<String> = mutableListOf();
        for(tree in this@MainActivity.cur_trees) {
            texts.add("${tree.taxon} (${tree.collection})");
        }
        this@MainActivity.tree_list.adapter = ArrayAdapter(this@MainActivity, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, texts);
    }

    override fun onCreate(saved_instance_state: Bundle?) {
        super.onCreate(saved_instance_state);
        this.setContentView(R.layout.activity_main);
        this.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        this.info_label = this.findViewById(R.id.InfoLabel);
        this.query_text = this.findViewById(R.id.QueryText);
        this.tree_list = this.findViewById(R.id.TreeList);

        this.info_label.text = "Loading...";

        this.query_text.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Called before the text is changed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Called as the text is being changed
                println("Text changed to: $s")
            }

            override fun afterTextChanged(s: Editable?) {
                this@MainActivity.loadByQuery(s.toString());
            }
        })

        this.tree_list.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>, v: View, index: Int, id: Long) {
                val tree = this@MainActivity.cur_trees[index];
                val maps_uri = "http://maps.google.com/maps?q=loc:${tree.lat},${tree.lng}(${tree.taxon})";
                val intent = Intent(Intent.ACTION_VIEW, maps_uri.toUri());
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                this@MainActivity.startActivity(intent);
            }
        });

        thread {
            try {
                this.map = MapContent();
                this.runOnUiThread {
                    this.info_label.text = "Listo!";
                    this.loadByQuery("");
                }
            }
            catch(e: Exception) {
                this.runOnUiThread {
                    this.info_label.text = "Error en la carga: ${e}";
                }
            }
        }
    }
}
