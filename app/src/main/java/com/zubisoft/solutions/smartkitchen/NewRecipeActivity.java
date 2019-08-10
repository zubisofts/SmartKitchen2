package com.zubisoft.solutions.smartkitchen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aminography.choosephotohelper.ChoosePhotoHelper;
import com.aminography.choosephotohelper.callback.ChoosePhotoCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.yalantis.ucrop.UCrop;
import com.zubisoft.solutions.smartkitchen.adapters.AddRecipeStepAdapter;
import com.zubisoft.solutions.smartkitchen.adapters.StepsClickListener;
import com.zubisoft.solutions.smartkitchen.model.CookStep;
import com.zubisoft.solutions.smartkitchen.model.Ingredient;
import com.zubisoft.solutions.smartkitchen.model.Recipe;
import com.zubisoft.solutions.smartkitchen.model.RecipeData;
import com.zubisoft.solutions.smartkitchen.util.SmartKitchenUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewRecipeActivity extends BaseActivity implements StepperLayout.StepperListener, StepsClickListener, RecipeFragment.ImageSourceListener, StepsFragment.OnAddImageListener {

    public static Uri imageResultUri;
    private StepperLayout mStepperLayout;
    private AddRecipeStepAdapter adapter;
    private int position;
    private RecipeData recipeData = new RecipeData();
    private ChoosePhotoHelper choosePhotoHelper;
    private FirebaseFirestore db;
    private List<CookStep> steps;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipe);

        mStepperLayout = findViewById(R.id.stepperLayout);
        adapter = new AddRecipeStepAdapter(getSupportFragmentManager(), this);
        mStepperLayout.setAdapter(adapter);
        adapter.addStep(new RecipeFragment(), "Add RecipeData Details");
        adapter.addStep(new IngredientsFragment(), "Add RecipeData Ingredients");
        adapter.addStep(new StepsFragment(), "Add Recipes Steps");
        mStepperLayout.setAdapter(adapter);
        mStepperLayout.setListener(this);

        dialog = new ProgressDialog(getApplicationContext());

        imageResultUri = null;

        choosePhotoHelper = ChoosePhotoHelper.with(NewRecipeActivity.this)
                .asFilePath()
                .build(new ChoosePhotoCallback<String>() {
                    @Override
                    public void onChoose(String photo) {
                        if (photo != null) {
                            imageResultUri = Uri.fromFile(new File(photo));
                            SmartKitchenUtil.startCrop(imageResultUri, NewRecipeActivity.this);

                        } else {
                            imageResultUri = null;
                        }
//                        File file=new File(photo);
//                        if (file.exists()) {
//                            //imageResultUri = Uri.fromFile(file);
//                        }
                    }
                });


    }


    @Override
    public void onCompleted(View completeButton) {
        saveRecipe();

    }

    @Override
    public void onError(VerificationError verificationError) {

    }

    @Override
    public void onStepSelected(int newStepPosition) {
        position = newStepPosition;
//        Toast.makeText(this, recipeData.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReturn() {
        finish();
    }

    @Override
    public void onNextClicked(Object mapData) {
        if (position == 0) {
            Recipe recipe = (Recipe) mapData;
            recipeData.setRecipe(recipe);
        }

        if (position == 1) {
            List<Ingredient> ingredients = (List) mapData;
            recipeData.setIngredients(ingredients);
        }

        if (position == 2) {
            List<CookStep> steps = (List) mapData;
            recipeData.setSteps(steps);
        }

        imageResultUri = null;
    }

    @Override
    public void onPreviousPressed(Object mapData) {

    }

    @Override
    public void onAddSteps(Object map) {
//        data.put("steps", map);

    }

    public void saveRecipe() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving your recipe, please wait");
        progressDialog.show();

        db = FirebaseFirestore.getInstance();
        final String imageUrl = recipeData.getRecipe().getImageUrl();

// Add a new document with a generated ID
        db.collection("recipes")
                .add(recipeData.getRecipe())
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
//                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        putImageInStorage(documentReference, documentReference.getId(), Uri.fromFile(new File(imageUrl)));
                        saveSteps(documentReference);
                        saveIngredients(documentReference);

//                        if (progressDialog.isShowing()) {
//                            progressDialog.dismiss();
//
////                            Snackbar.make(mStepperLayout, "Recipe saved successfully", Snackbar.LENGTH_SHORT).show();
////                            onBackPressed();
//                        }
//                        putImageInStorage(documentReference, documentReference.getId(), imageResultUri);
//                        uploadAndUpdateStep(documentReference,documentReference.getId());
//                        Toast.makeText(getApplicationContext(), documentReference.getId(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        progressDialog.cancel();
                        Snackbar.make(mStepperLayout, "Recipe was not saved", Snackbar.LENGTH_SHORT).show();
//                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    private void saveIngredients(DocumentReference documentReference) {
        List<Ingredient> ingredients = recipeData.getIngredients();
        for (final Ingredient ingredient : ingredients) {
            documentReference.collection("ingredients")
                    .add(ingredient).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    String id = documentReference.getId();
                    documentReference.update("id", id);
                }
            });
        }
    }

    private void saveSteps(final DocumentReference recipeReference) {
        List<CookStep> cookSteps = recipeData.getSteps();
        for (final CookStep step : cookSteps) {
            recipeReference.collection("steps")
                    .add(step).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference stepReference) {
                    String id = stepReference.getId();
                    stepReference.update("id", id);
                    uploadAndUpdateStep(recipeReference, stepReference);
                }
            });
        }
    }

    private void uploadAndUpdateStep(final DocumentReference recipeRef, final DocumentReference stepRef) {
        stepRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot snapshot = task.getResult();
                            CookStep step = snapshot.toObject(CookStep.class);
                            putStepsImageInStorage(recipeRef, stepRef, step);
                        }
                    }
                });
    }


    private void putImageInStorage(final DocumentReference recipe_ref, final String recipe_id, final Uri uri/*File file*/) {

//        Snackbar.make(btnCreateGroup, "Updating group resources...", Snackbar.LENGTH_SHORT).show();

        StorageReference storageReference =
                FirebaseStorage.getInstance()
                        .getReference("smart_kitchen_files")
                        .child("recipes")
                        .child(recipe_id)
                        .child("image")
                        .child(uri.getLastPathSegment());

        storageReference.putFile(/*Uri.fromFile(file)*/ uri).addOnCompleteListener(this,
                new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
//                            Toast.makeText(getApplicationContext(), "Photo uploaded", Toast.LENGTH_SHORT).show();
                            SmartKitchenUtil.deleteFile(new File(uri.getPath()));
                            SmartKitchenUtil.mCurrentPhotoPath = null;

                            //  Get the downloadable url from uploaded file
                            FirebaseStorage.getInstance().getReference(task.getResult().getMetadata().getPath())
                                    .getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    //  On success of uploading the image, we now have to upload the recipe data
                                    Map<String, Object> value = new HashMap<>();
                                    value.put("imageUrl", task.getResult().toString());
                                    recipe_ref.update(value)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Snackbar.make(mStepperLayout, "Recipe upload successful", Snackbar.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                }
                            });
                        } else {
                            //Log.w(TAG, "",
                            //task.getException());
                            Toast.makeText(getApplicationContext(), task.getException().toString() /*"Image upload task was not successful."*/, Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {


//                dialog.setMax((int)taskSnapshot.getTotalByteCount());
//                dialog.setProgress((int)taskSnapshot.getBytesTransferred());
//                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
//                    @Override
//                    public void onShow(DialogInterface dialog) {
//
//                    }
//                });
//                dialog.show();
                Toast.makeText(NewRecipeActivity.this, taskSnapshot.getTotalByteCount() + " Bytes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void putStepsImageInStorage(final DocumentReference recipeRef, final DocumentReference stepRef, final CookStep step/*File file*/) {

//        Snackbar.make(btnCreateGroup, "Updating group resources...", Snackbar.LENGTH_SHORT).show();

        if (step.getImageUrl().equals(""))
            return;

        StorageReference storageReference =
                FirebaseStorage.getInstance()
                        .getReference("smart_kitchen_files")
                        .child("recipes")
                        .child(recipeRef.getId())
                        .child("step_images")
                        .child((Uri.fromFile(new File(step.getImageUrl())).getLastPathSegment()));

        storageReference.putFile(/*Uri.fromFile(file)*/ Uri.fromFile(new File(step.getImageUrl()))).addOnCompleteListener(this,
                new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
//
                            //  Get the downloadable url from uploaded file
                            FirebaseStorage.getInstance().getReference(task.getResult().getMetadata().getPath())
                                    .getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    //  On success of uploading the image, we now have to upload the recipe data
                                    stepRef.update("imageUrl", task.getResult().toString());
                                    Toast.makeText(NewRecipeActivity.this, "Done", Toast.LENGTH_LONG).show();

                                }
                            });
                        } else {
                            //Log.w(TAG, "",
                            //task.getException());
                            Toast.makeText(getApplicationContext(), task.getException().toString() /*"Image upload task was not successful."*/, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onPickImageSource() {
        choosePhotoHelper.showChooser();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == UCrop.REQUEST_CROP) {
                imageResultUri = UCrop.getOutput(data);
            } else if (requestCode == UCrop.RESULT_ERROR) {
                Toast.makeText(this, "Error getting image", Toast.LENGTH_SHORT).show();
            } else {
                choosePhotoHelper.onActivityResult(requestCode, resultCode, data);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        choosePhotoHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onAddImage() {
        choosePhotoHelper.showChooser();
    }

    public Uri getImageResultUri() {
        return imageResultUri;
    }
}
