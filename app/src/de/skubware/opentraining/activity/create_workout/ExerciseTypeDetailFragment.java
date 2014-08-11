/**
 * 
 * This is OpenTraining, an Android application for planning your your fitness training.
 * Copyright (C) 2012-2014 Christian Skubich
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package de.skubware.opentraining.activity.create_workout;


import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Map;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.mime.MimeUtil;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import de.skubware.opentraining.BuildConfig;
import de.skubware.opentraining.R;
import de.skubware.opentraining.basic.ExerciseType;
import de.skubware.opentraining.basic.ExerciseType.ExerciseSource;
import de.skubware.opentraining.basic.FitnessExercise;
import de.skubware.opentraining.basic.Muscle;
import de.skubware.opentraining.basic.SportsEquipment;
import de.skubware.opentraining.basic.Workout;
import de.skubware.opentraining.db.DataHelper;
import de.skubware.opentraining.db.DataProvider;
import de.skubware.opentraining.db.IDataProvider;
import de.skubware.opentraining.db.rest.ExerciseTypeGSONSerializer;
import de.skubware.opentraining.db.rest.LanguageGSONDeserializer;
import de.skubware.opentraining.db.rest.MuscleGSONDeserializer;
import de.skubware.opentraining.db.rest.ServerModel;
import de.skubware.opentraining.db.rest.ServerModel.Equipment;
import de.skubware.opentraining.db.rest.ServerModel.Language;
import de.skubware.opentraining.db.rest.ServerModel.MuscleCategory;
import de.skubware.opentraining.db.rest.SportsEquipmentGSONDeserializer;

/**
 * A fragment representing a single ExerciseType detail screen. This fragment is
 * either contained in a {@link ExerciseTypeListActivity} in two-pane mode (on
 * tablets) or a {@link ExerciseTypeDetailActivity} on handsets.
 */
public class ExerciseTypeDetailFragment extends Fragment {
	/** Tag for logging */
	public static final String TAG = "ExerciseTypeDetailFragment";

	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_EXERCISE = "exercise";

	public static final String ARG_WORKOUT = "workout";

	/** Result-Code when the exercises changed (e.g. one has been deleted) */
	public static final int RESULT_EXERCISE_CHANGED = 93278734;
	
	/** Key for passing the exercise that has been deleted. */
	public static final String ARG_DELETED_EXERCISE = "delted_ex";
	
	/**
	 * The {@link ExerciseType} this fragment is presenting.
	 */
	private ExerciseType mExercise;
	private Workout mWorkout;

	private GestureDetector mGestureScanner;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ExerciseTypeDetailFragment() {
	}

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of changes.
	 */
	public interface Callbacks {
		/**
		 * Callback for when the Workout has changed.
		 */
		public void onWorkoutChanged(Workout w);
		
		/** 
		 * Callback, when an exercise has been deleted. 
		 */
		public void onExerciseDeleted(ExerciseType deletedExercise);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setHasOptionsMenu(true);

		mExercise = (ExerciseType) getArguments().getSerializable(ExerciseTypeDetailFragment.ARG_EXERCISE);
		mWorkout = (Workout) getArguments().getSerializable(ExerciseTypeDetailFragment.ARG_WORKOUT);

		this.getActivity().setTitle(mExercise.getLocalizedName());
	}

	/** Saves the state of this Fragment, e.g. when screen orientation changed. */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(ExerciseTypeDetailFragment.ARG_EXERCISE, mExercise);
		outState.putSerializable(ExerciseTypeDetailFragment.ARG_WORKOUT, mWorkout);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_exercisetype_detail, container, false);

		// show the current exercise

		ImageView imageview = (ImageView) rootView.findViewById(R.id.imageview);

		// set gesture detector
		this.mGestureScanner = new GestureDetector(this.getActivity(), new ExerciseDetailOnGestureListener(this, imageview, mExercise));

		// Images
		if (!mExercise.getImagePaths().isEmpty()) {
			DataHelper data = new DataHelper(getActivity());
			imageview.setImageDrawable(data.getDrawable(mExercise.getImagePaths().get(0).toString()));
		} else {
			imageview.setImageResource(R.drawable.ic_launcher);
		}


		rootView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return mGestureScanner.onTouchEvent(event);
			}
		});

		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		// MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.exercise_detail_menu, menu);

		// configure menu_item_add_exercise
		MenuItem menu_item_add_exercise = (MenuItem) menu.findItem(R.id.menu_item_add_exercise);
		menu_item_add_exercise.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {

				// assert, that an exercise was choosen
				if (mExercise == null) {
					Log.wtf(TAG, "No exercise has been choosen. This should not happen");
					return true;
				}

				// add exercise to workout or create a new one
				if (mWorkout == null) {
					SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
					String defaultWorkoutName =  settings.getString("default_workout_name", "Workout");

					mWorkout = new Workout(defaultWorkoutName, new FitnessExercise(mExercise));
				} else {

					// assert that there is not already such an exercise in the
					// workout
					for (FitnessExercise fEx : mWorkout.getFitnessExercises()) {
						if (fEx.getExType().equals(mExercise)) {
							Toast.makeText(getActivity(), getString(R.string.exercise_already_in_workout), Toast.LENGTH_LONG).show();
							return true;
						}
					}

					mWorkout.addFitnessExercise(new FitnessExercise(mExercise));
				}

				// update Workout in Activity
				if (getActivity() instanceof Callbacks) {
					// was launched by ExerciseTypeListActivity
					((Callbacks) getActivity()).onWorkoutChanged(mWorkout);
				} else {
					// was launched by ExerciseTypeDetailActivity
					Intent i = new Intent();
					i.putExtra(ExerciseTypeListActivity.ARG_WORKOUT, mWorkout);
					getActivity().setResult(Activity.RESULT_OK, i);
					getActivity().finish();
				}

				Toast.makeText(getActivity(),
						getString(R.string.exercise) + " " + mExercise.getLocalizedName() + " " + getString(R.string.has_been_added),
						Toast.LENGTH_SHORT).show();

				return true;
			}
		});
		
		
		
		// configure menu_item_license_info
		MenuItem menu_item_license_info = (MenuItem) menu.findItem(R.id.menu_item_license_info);
		menu_item_license_info.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle(getString(R.string.license_info));
				
				String license = "";

				if (mExercise.getImageLicenseMap().values().iterator().hasNext()) {
					license = mExercise.getImageLicenseMap().values().iterator().next().toString();
				} else {
					license = getString(R.string.no_license_available);
				}
				
				builder.setMessage(license);
				builder.create().show();

				return true;
			}
		});

		// configure menu_item_description
		MenuItem menu_item_description = (MenuItem) menu.findItem(R.id.menu_item_description);
		menu_item_description.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				if(mExercise.getDescription() == null || mExercise.getDescription().equals("")){
					Toast.makeText(getActivity(), getString(R.string.no_description_available), Toast.LENGTH_LONG).show();
					return true;
				}
				
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle(getString(R.string.description));
				
				
				builder.setMessage(Html.fromHtml(mExercise.getDescription()));
				builder.create().show();

				return true;
			}
		});
	
		// configure menu_item_delete_exercise
		if(mExercise != null  && mExercise.getExerciseSource() == ExerciseSource.CUSTOM){
			MenuItem menu_item_delete_exercise = (MenuItem) menu.findItem(R.id.menu_item_delete_exercise);
			menu_item_delete_exercise.setVisible(true);
			menu_item_delete_exercise.setOnMenuItemClickListener(new OnMenuItemClickListener(){
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					IDataProvider dataProvider = new DataProvider(getActivity());
					dataProvider.deleteCustomExercise(mExercise);
					
					if (getActivity() instanceof Callbacks) {
						// was launched by ExerciseTypeListActivity
						((Callbacks) getActivity()).onExerciseDeleted(mExercise);
					} else {
						// was launched by ExerciseTypeDetailActivity
						Intent i = new Intent();
						i.putExtra(ARG_DELETED_EXERCISE, mExercise);
						getActivity().setResult(RESULT_EXERCISE_CHANGED, i);
						getActivity().finish();
					}
					
										
					return false;
				}
			});
		}
		
		
		// configure menu_item_upload_exercise
		MenuItem menu_item_upload_exercise = (MenuItem) menu.findItem(R.id.menu_item_upload_exercise);
		menu_item_upload_exercise.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				
				UploadExerciseOperation exUpload = new UploadExerciseOperation();
	            
				exUpload.execute(mExercise);

				/*if(mExercise.getDescription() == null || mExercise.getDescription().equals("")){
					Toast.makeText(getActivity(), getString(R.string.no_description_available), Toast.LENGTH_LONG).show();
					return true;
				}*/
				
				//AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				//builder.setTitle(getString(R.string.description));
				
				
				//builder.setMessage(Html.fromHtml(mExercise.getDescription()));
				//builder.create().show();

				return true;
			}
		});
	
		// configure menu_item_send_exercise_feedback
			MenuItem menu_item_delete_exercise = (MenuItem) menu.findItem(R.id.menu_item_send_exercise_feedback);
			menu_item_delete_exercise.setVisible(true);
			menu_item_delete_exercise.setOnMenuItemClickListener(new OnMenuItemClickListener(){
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					
					FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
					Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag("dialog");
					if (prev != null) {
						ft.remove(prev);
					}
					ft.addToBackStack(null);

					// Create and show the dialog.
					DialogFragment newFragment = SendExerciseFeedbackDialogFragment.newInstance(mExercise);
					newFragment.show(ft, "dialog");

					return false;
				}
			});
		
		
		

		
	}

	public interface WgerRestService {		
		  @POST("/exercise/")
		  public Response createExercise(@Body ExerciseType exercise);
		  
		  @GET("/equipment/")
		  public ServerModel.Equipment[] getEquipment();
		  
		  @GET("/exercisecategory/")
		  public ServerModel.MuscleCategory[] getMuscles();
		  
		  @GET("/language/")
		  public ServerModel.Language[] getLanguages();
	}
	
	
	 private class UploadExerciseOperation extends AsyncTask<ExerciseType, Void, Throwable> {
		  private final ProgressDialog dialog = new ProgressDialog(getActivity());
		  
		  
		  protected void onPreExecute() {
			     this.dialog.setMessage("Uploading exercise ...");
			     this.dialog.show();
			  }
		  
		
		/**
		 * @return Null if everything went fine, the original exception otherwise.
		 */
		@Override
		protected Throwable doInBackground(ExerciseType... exercise) {

			// prepare GsonBuilder
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(ExerciseType.class, new ExerciseTypeGSONSerializer());
			gsonBuilder.registerTypeAdapter(ServerModel.Equipment[].class, new SportsEquipmentGSONDeserializer());
			gsonBuilder.registerTypeAdapter(ServerModel.MuscleCategory[].class, new MuscleGSONDeserializer());
			gsonBuilder.registerTypeAdapter(ServerModel.Language[].class, new LanguageGSONDeserializer());
			gsonBuilder.setPrettyPrinting();

			Gson gson = gsonBuilder.create();

			GsonConverter converter = new GsonConverter(gson);

			RestAdapter.Builder builder = new RestAdapter.Builder().setConverter(converter).setEndpoint("http://preview.wger.de/api/v2/")
					.setRequestInterceptor(new RequestInterceptor() {
						@Override
						public void intercept(RequestFacade requestFacade) {
							requestFacade.addHeader("Authorization", "Token ba1ce753f54ba3b8ee4af301f07c58628a1c01bf");
						}
					});

			// only log if debug-build
			// (otherwise auth-token appears in log)
			if (BuildConfig.DEBUG) {
				builder.setLog(new AndroidLog("WgerRestService")).setLogLevel(LogLevel.FULL);
			}

			RestAdapter restAdapter = builder.build();

			WgerRestService service = restAdapter.create(WgerRestService.class);

			// get server model of SportsEquipment
			ServerModel.Equipment[] serverEquipment = service.getEquipment();
			Map<SportsEquipment, Equipment> eqMap = Equipment.getEquipmentMap(serverEquipment, getActivity());
			ExerciseTypeGSONSerializer.setEquipmentMap(eqMap);

			// get server model of Muscle(categories)
			ServerModel.MuscleCategory[] serverMuscles = service.getMuscles();
			Map<Muscle, MuscleCategory> muscleMap = MuscleCategory.getMuscleMap(serverMuscles, getActivity());
			ExerciseTypeGSONSerializer.setMuscleMap(muscleMap);

			for (Muscle m : muscleMap.keySet()) {
				Log.e(TAG, m.toString() + " = " + muscleMap.get(m) + "\n");
			}

			// get server model of Languages
			ServerModel.Language[] serverLanguages = service.getLanguages();
			Map<Locale, Language> languageMap = Language.getLanguageMap(serverLanguages, getActivity());
			ExerciseTypeGSONSerializer.setLanguageMap(languageMap);

			try {
				service.createExercise(exercise[0]);		
			} catch (RetrofitError retEr) {
				if(retEr.getCause() != null)
					return retEr.getCause();
				else
					return retEr;
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Throwable ex) {
			dialog.dismiss();

			AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
			String msg;
			String title;
			if (ex == null) {
				// everything went fine
				title = "Upload successfull";
				msg = "Upload finished";
			} else {
				title = "Upload failed";
				if(ex instanceof RetrofitError){
					// show server response to user
					Response response = ((RetrofitError) ex).getResponse();
					msg = response.getReason() + ": " + getBodyString(response);
				}else{
					// show custom error message if problem is known
					msg = ex.getMessage();
				}	
			}
			alertDialog.setMessage(msg);
			alertDialog.setTitle(title);
			alertDialog.create().show();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
		
		
		/* Helper method for parsing the response body */
		private String getBodyString(Response response) {

			TypedInput body = response.getBody();

			if (body != null) {

				if (!(body instanceof TypedByteArray)) {
					Log.e(TAG, "Could not parse.");
					return "";
				}

				byte[] bodyBytes = ((TypedByteArray) body).getBytes();
				String bodyMime = body.mimeType();
				String bodyCharset = MimeUtil.parseCharset(bodyMime);
				try {
					return new String(bodyBytes, bodyCharset);
				} catch (UnsupportedEncodingException e) {
					Log.e(TAG, "Could not parse.");
					return "";
				}
			}
			return null;

		}
		
	}

}
