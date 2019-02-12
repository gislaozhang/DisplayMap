/* Copyright 2016 Esri
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.esri.arcgisruntime.sample.displaymap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;

public class MainActivity extends AppCompatActivity {

    private MapView mMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMapView = (MapView) findViewById(R.id.mapView);
//        displayMapByWebMapUrl();
        displayMapByLayerUrl();
    }

    /**
     * 使用图层URL来显示地图
     */
    private void displayMapByLayerUrl() {
        String urlpath = "http://cache1.arcgisonline.cn/arcgis/rest/services/ChinaOnlineCommunity_Mobile/MapServer";
        ArcGISTiledLayer layer = new ArcGISTiledLayer(urlpath);
        Basemap basemap = new Basemap(layer);
        ArcGISMap map = new ArcGISMap(basemap);
        // set the map to be displayed in this view
        mMapView.setMap(map);
    }

    /**
     * 使用web map URL来显示地图
     */
    private void displayMapByWebMapUrl() {
        String webMapURL = "https://www.arcgis.com/home/webmap/viewer.html?webmap=69fdcd8e40734712aaec34194d4b988c";
        Basemap basemap = new Basemap(webMapURL);
        ArcGISMap map = new ArcGISMap(basemap);
        mMapView.setMap(map);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.dispose();
    }
}
