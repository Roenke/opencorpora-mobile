package org.opencorpora.data.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencorpora.BuildConfig;
import org.opencorpora.data.TaskType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class OpenCorporaClient {
    private static final String LOG_TAG = "OpenCorporaClient";
    private static final String TYPES_URL = BuildConfig.server_address + "api/pool_types.php";
    private static final String ACTUALIZE_URL = BuildConfig.server_address + "api/tasks.php";

    private OpenCorporaRequestQueue mQueue;

    public OpenCorporaClient(Context context){
        mQueue = OpenCorporaRequestQueue.getInstance(context);
    }

    public ArrayList<TaskType> getTypes(final String uid, final String token){
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request =
                new JsonObjectRequest(Request.Method.GET ,TYPES_URL, future, future){
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("uid", uid);
                        params.put("token", token);
                        return params;
                    }
                };
        mQueue.getRequestQueue().add(request);
        ArrayList<TaskType> result = new ArrayList<>();
        try{
            JSONObject response = future.get(10, TimeUnit.SECONDS);
            JSONArray types = response.getJSONArray("items");
            for(int i = 0; i < types.length(); ++i){
                int id = types.getJSONObject(i).getInt("type_id");
                String name = types.getJSONObject(i).getString("name");
                int complexity = types.getJSONObject(i).getInt("complexity");
                TaskType type = new TaskType(id, name, complexity);
                result.add(type);
            }
            Log.i(LOG_TAG, "Receive " + types.length() + " items");
        } catch (InterruptedException | ExecutionException | TimeoutException | JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    // Returns only actual tasks
    public ArrayList<Integer> actualizeTasks(ArrayList<Integer> taskIds,
                                             final String uid,
                                             final String token){
        ArrayList<Integer> result = new ArrayList<>();
        JSONObject taskIdsBody = new JSONObject();
        JSONArray array = new JSONArray(taskIds);
        try {
            taskIdsBody.put("items", array);
            RequestFuture<JSONObject> future = RequestFuture.newFuture();
            JsonObjectRequest request =
                    new JsonObjectRequest(Request.Method.POST ,
                            ACTUALIZE_URL,
                            taskIdsBody,
                            future,
                            future){
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("uid", uid);
                            params.put("token", token);
                            return params;
                        }
                    };
            mQueue.getRequestQueue().add(request);

            JSONObject response = future.get(10, TimeUnit.SECONDS);
            Log.i(LOG_TAG, "Response received.");
            JSONArray actual = response.getJSONArray("items");
            for(int i = 0; i < actual.length(); ++i){
                int id = actual.getInt(i);
                result.add(id);
            }
        } catch (JSONException | InterruptedException | TimeoutException | ExecutionException e) {
            e.printStackTrace();
        }

        Log.i(LOG_TAG, "Task actual: " + result.size() + " / " + taskIds.size());

        return result;
    }
}

