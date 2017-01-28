package net.simplifiedcoding.androidmysqlsync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Belal on 1/27/2017.
 */

public class NetworkStateChecker extends BroadcastReceiver {

    //context and database helper object
    private Context context;
    private DatabaseHelper db;


    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        db = new DatabaseHelper(context);

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        //if there is a network
        if (activeNetwork != null) {
            //if connected to wifi or mobile data plan
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {

                //getting all the unsynced names
                Cursor cursor = db.getUnsyncedNames();
                if (cursor.moveToFirst()) {
                    do {
                        //calling the method to save the unsynced name to MySQL
                        saveName(
                                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)),
                                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME))
                        );
                    } while (cursor.moveToNext());
                }
            }
        }
    }

    /*
    * method taking two arguments
    * name that is to be saved and id of the name from SQLite
    * if the name is successfully sent
    * we will update the status as synced in SQLite
    * */
    private void saveName(final int id, final String name) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, MainActivity.URL_SAVE_NAME,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                //updating the status in sqlite
                                db.updateNameStatus(id, MainActivity.NAME_SYNCED_WITH_SERVER);

                                //sending the broadcast to refresh the list
                                context.sendBroadcast(new Intent(MainActivity.DATA_SAVED_BROADCAST));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                return params;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

}
