package com.barmej.streetissues;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.barmej.streetissues.Model.Issues;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class AddNewIssuesActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ConstraintLayout constraintLayout;
    private EditText mIssuesNameEditText;
    private EditText mIssuesDescriptionEditText;
    private EditText mIssuesDateEditText;
    private ImageView mIssuesImageView;
    private Button mAddIssuesButton;
    private static final String TAG=AddNewIssuesActivity.class.getSimpleName();
    private static final int PERMISSION_REQUEST_ACCESS_LOCATION=1;
    private static final int PERMISSION_REQUEST_READ_STORAGE=2;
    private static final int REQUEST_GET_PHOTO=3;
    private boolean mLocationPermissionGranted;
    private boolean mReadStoragePermissioGranted;
    private Uri mIssuesPhotoUri;
    private ProgressDialog mDialog;
    private FusedLocationProviderClient mLocationProviderClient;
    private Location mLastKnowLocation;
    private LatLng mSelectedLatLng;
    private GoogleMap mGoogleMap;
    private static final LatLng DEFUALTLOCATION=new LatLng(29.3760641,47.96643571);
    private MapView mMapView;
    private Date mIssuesDate;




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_issue);
        constraintLayout=findViewById(R.id.add_issues_constraint_layout);
        mIssuesNameEditText=findViewById(R.id.nameEditText);
        mIssuesDescriptionEditText=findViewById(R.id.descriptionEditText);
        mIssuesDateEditText=findViewById(R.id.edit_text_date);
        mIssuesImageView=findViewById(R.id.issuesImageView);
        mAddIssuesButton=findViewById(R.id.addIssuesButton);
        mMapView=findViewById(R.id.chooseMapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        requestExternalStoragePermission();
        requestLocationPermission();
        mIssuesImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LaunchGalleryIntent();
            }
        });

        mIssuesDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIssuesDateEditText.setError(null);
                showDateChooserDialog();
            }
        });

        mAddIssuesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(mIssuesNameEditText.getText()))
                {
                    mIssuesNameEditText.setError(getString(R.string.error_msg_name));

                }else if(TextUtils.isEmpty(mIssuesDescriptionEditText.getText()))
                {
                    mIssuesDescriptionEditText.setError(getString(R.string.error_msg_description));

                }else if(TextUtils.isEmpty(mIssuesDateEditText.getText()))
                {
                    mIssuesDateEditText.setError(getString(R.string.error_msg_date));

                }else{
                    if(mIssuesPhotoUri!=null)
                        addIssuesToFirebase();
                }
            }
        });

        mLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
    }

    private void requestLocationPermission(){
        mLocationPermissionGranted=false;
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            mLocationPermissionGranted=true;
        }else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_REQUEST_ACCESS_LOCATION);
        }
    }

    private void requestExternalStoragePermission(){
        mReadStoragePermissioGranted=false;
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            mReadStoragePermissioGranted=true;
        }else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_REQUEST_READ_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_REQUEST_ACCESS_LOCATION:
                mLocationPermissionGranted=false;
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    mLocationPermissionGranted=true;
                    requestDeviceCurrentLocation();
                    break;
                }
            case PERMISSION_REQUEST_READ_STORAGE:
                mReadStoragePermissioGranted=false;
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    mReadStoragePermissioGranted=true;
                    break;
                }
        }
    }

    private void LaunchGalleryIntent(){
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent,getString(R.string.choose_photo)),REQUEST_GET_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_GET_PHOTO){
            if(resultCode==RESULT_OK){

                try {
                    mIssuesPhotoUri=data.getData();
                    mIssuesImageView.setImageURI(mIssuesPhotoUri);
                }catch (Exception e){
                    Snackbar.make(constraintLayout,R.string.photo_selection_error,Snackbar.LENGTH_LONG).show();
                }
            }
        }
    }

    private void addIssuesToFirebase(){
        FirebaseStorage firebaseStorage=FirebaseStorage.getInstance();
        StorageReference storageReference=firebaseStorage.getReference().child("StreetIssuesPhoto");
        final StorageReference photoStorageReference=storageReference.child(UUID.randomUUID().toString());
        final FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
        mDialog=new ProgressDialog(this);
        mDialog.setIndeterminate(true);
        mDialog.setTitle(R.string.app_name);
        mDialog.setMessage(getString(R.string.uploading_photo));
        mDialog.show();

        photoStorageReference.putFile(mIssuesPhotoUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    mDialog.dismiss();
                    photoStorageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()) {
                                final Issues issues = new Issues();
                                issues.setName(mIssuesNameEditText.getText().toString());
                                issues.setDescription(mIssuesDescriptionEditText.getText().toString());
                                issues.setDate(new Timestamp(mIssuesDate));
                                issues.setPhoto(task.getResult().toString());
                                issues.setLocation(new GeoPoint(mSelectedLatLng.latitude,mSelectedLatLng.longitude));

                                firebaseFirestore.collection("Issues").add(issues).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {

                                        if (task.isSuccessful()) {
                                            Snackbar.make(constraintLayout, R.string.dd_issues_successful, Snackbar.LENGTH_SHORT).addCallback(new Snackbar.Callback() {
                                                @Override
                                                public void onDismissed(Snackbar transientBottomBar, int event) {
                                                    super.onDismissed(transientBottomBar, event);
                                                    finish();
                                                }
                                            }).show();
                                        } else {
                                            Snackbar.make(constraintLayout, R.string.dd_issues_faild, Snackbar.LENGTH_SHORT).show();
                                            mDialog.dismiss();
                                        }
                                    }
                                });


                            }else
                            {
                                Snackbar.make(constraintLayout,R.string.uploading_task_failed,Snackbar.LENGTH_SHORT).show();
                                mDialog.dismiss();
                            }
                        }
                    });
                }else
                {
                    Snackbar.make(constraintLayout,R.string.uploadin_photo_failed,Snackbar.LENGTH_SHORT).show();
                    mDialog.dismiss();
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void requestDeviceCurrentLocation(){
         Task<Location> locationResult=mLocationProviderClient.getLastLocation();
         locationResult.addOnSuccessListener(new OnSuccessListener<Location>() {
             @Override
             public void onSuccess(Location location) {
               if(location!=null){
                   mLastKnowLocation=location;
                   mSelectedLatLng=new LatLng(mLastKnowLocation.getLatitude(),mLastKnowLocation.getLongitude());
                   mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mSelectedLatLng,15));
                   MarkerOptions markerOptions=new MarkerOptions();
                   markerOptions.position(mSelectedLatLng);
                   markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
               }else{
                   mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFUALTLOCATION,15));
               }
             }
         });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
      mGoogleMap=googleMap;
        if(mLocationPermissionGranted){
          requestDeviceCurrentLocation();
      }
      mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
          @Override
          public void onMapClick(LatLng latLng) {
              mSelectedLatLng=latLng;
              mGoogleMap.clear();
              MarkerOptions markerOptions=new MarkerOptions();
              markerOptions.position(mSelectedLatLng);
              markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
              mGoogleMap.addMarker(markerOptions);
          }
      });
    }

    private void showDateChooserDialog(){
        Calendar calendar=Calendar.getInstance();
        int startYear=calendar.get(Calendar.YEAR);
        int startMonth=calendar.get(Calendar.MONTH);
        int startDay=calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog=new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar newCalender =Calendar.getInstance();
                newCalender.set(year,month,dayOfMonth);
                mIssuesDate=newCalender.getTime();
                DateFormat dateFormat=DateFormat.getDateInstance(DateFormat.DATE_FIELD);
                mIssuesDateEditText.setText(dateFormat.format(newCalender.getTime()));
            }
        },startYear,startMonth,startDay);
        datePickerDialog.show();

    }
    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

}
