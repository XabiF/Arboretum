package com.xabif.arboretum
import android.util.Log
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.util.stream.Collectors

;

class MapContent {
    private val cnt: JsonObject;

    init {
        val url = URL("https://www.ingunet.net/arboretum/v_elemento.json")
        val strm = url.openStream()!!
        val result = BufferedReader(InputStreamReader(strm)).lines().collect(Collectors.joining("\n"))
        Log.d("demo2", result.toString());
        this.cnt = JsonParser.parseString(result).asJsonObject;
    }

    fun lookup(query: String) : List<Tree> {
        val trees: MutableList<Tree> = mutableListOf();

        val features = this.cnt.getAsJsonArray("features");
        Log.d("demo3", features.toString());
        for(feature in features) {
            Log.d("demo", feature.toString());

            val properties = feature.asJsonObject.getAsJsonObject("properties");
            val taxon = properties.get("taxon").asString;

            if(taxon.contains(query, ignoreCase = true)) {
                val collection = properties.get("coleccion").asString;

                val geometry = feature.asJsonObject.getAsJsonObject("geometry");
                val coordinates = geometry.get("coordinates").asJsonArray;
                val coordinates_1st = coordinates.get(0);
                val pos = if(coordinates_1st.isJsonArray) {
                    coordinates_1st.asJsonArray
                }
                else {
                    coordinates
                };
                val pos_coords = doubleArrayOf(pos.get(0).asDouble, pos.get(1).asDouble);
                val coords = CoordinateConverter.convert(pos_coords);

                val tree = Tree(taxon, collection, coords[0], coords[1]);
                trees.add(tree);
            }
        }

        return trees;
    }
}
