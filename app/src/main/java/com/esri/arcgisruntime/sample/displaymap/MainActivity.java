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
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalItem;
import java.io.File;
import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.MobileMapPackage;

public class MainActivity extends AppCompatActivity {

    private MapView mMapView;
    private SceneView mSceneView;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String FILE_EXTENSION = ".mmpk";
    private static File extStorDir;
    private static String extSDCardDirName;
    private static String filename;
    private static String mmpkFilePath;
    // define permission to request
    String[] reqPermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private MobileMapPackage mapPackage;
    private int requestCode = 2;

    /**
     * Create the mobile map package file location and name structure
     */
    private static String createMobileMapPackageFilePath() {
        return extStorDir.getAbsolutePath() + File.separator + extSDCardDirName + File.separator + filename
                + FILE_EXTENSION;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // retrieve the MapView from layout
        mMapView = (MapView) findViewById(R.id.mapView);
//        mSceneView = findViewById(R.id.sceneView);
//        displayMapByWebMapUrl();
//        displayMapByLayerUrl();
//        displayMapByPortalItem();
        displayMapByMMPK();
    }

    /**
     * 使用mmpk来显示地图
     */
    private void displayMapByMMPK() {
        // get sdcard resource name
        extStorDir = Environment.getExternalStorageDirectory();
        // get the directory
        extSDCardDirName = this.getResources().getString(R.string.config_data_sdcard_offline_dir);
        // get mobile map package filename
        filename = this.getResources().getString(R.string.yellowstone_mmpk);
        // create the full path to the mobile map package file
        mmpkFilePath = createMobileMapPackageFilePath();

        // For API level 23+ request permission at runtime
        if (ContextCompat.checkSelfPermission(MainActivity.this, reqPermission[0]) == PackageManager.PERMISSION_GRANTED) {
            loadMobileMapPackage(mmpkFilePath);
        } else {
            // request permission
            ActivityCompat.requestPermissions(MainActivity.this, reqPermission, requestCode);
        }
    }

    /**
     * Handle the permissions request response
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadMobileMapPackage(mmpkFilePath);
        } else {
            // report to user that permission was denied
            Toast.makeText(MainActivity.this, getResources().getString(R.string.location_permission_denied),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 加载移动地图包
     *
     * @param mmpkFile Full path to mmpk file
     */
    private void loadMobileMapPackage(String mmpkFile) {

        // create the mobile map package
        mapPackage = new MobileMapPackage(mmpkFile);
        // load the mobile map package asynchronously
        mapPackage.loadAsync();

        // add done listener which will invoke when mobile map package has loaded
        mapPackage.addDoneLoadingListener(new Runnable() {
            @Override
            public void run() {
                // check load status and that the mobile map package has maps
                if (mapPackage.getLoadStatus() == LoadStatus.LOADED && !mapPackage.getMaps().isEmpty()) {
                    // add the map from the mobile map package to the MapView
                    mMapView.setMap(mapPackage.getMaps().get(0));
                } else {
                    // log an issue if the mobile map package fails to load
                    Log.e(TAG, mapPackage.getLoadError().getMessage());
                }
            }
        });

    }

    /**
     * 使用PortalItem来显示地图
     */
    private void displayMapByPortalItem() {
        // get the portal url and portal item from ArcGIS online
        Portal portal = new Portal("http://www.arcgis.com/", false);
        PortalItem portalItem = new PortalItem(portal, "a13c3c3540144967bc933cb5e498b8e4");
        // create scene from a portal item
        ArcGISScene scene = new ArcGISScene(portalItem);
        mSceneView.setScene(scene);
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
//        mSceneView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.resume();
//        mSceneView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.dispose();
//        mSceneView.dispose();
    }
}

