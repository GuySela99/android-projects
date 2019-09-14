package com.example.shoppinglist2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {
    HashMap<Category,ArrayList<Product>> store;// represent the categories and the product that store in hash map
    DrawerLayout drawerLayout;// reference to the drawer that get open when there is slide or menu click
    NavigationView navigationView;
    CoordinatorLayout coordinatorLayout;// reference to layout that coordinat between the tosts that item add and the list.
    String fullName;
    //those name references to the list of categories that pop up when the user want to add new product.
    StoreAdapter storeAdapter;
    ListView listView;
    ArrayAdapter<String> listAdapter;
    ArrayList<String> categoryList;
    EditText catagoryTe;
    Category categoryInDataBase;
    boolean firstTimeRunning = true;// use this boolean because i wanted that list will download only once.
    //TAG - for test.
    String TAG = "MainActivity";
    int _currenPostion =0;
    boolean mychange = false;

    /**
     * references to the user account and the user data base in the cloud.
     */
    public FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    public FirebaseAuth.AuthStateListener authStateListener;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final RecyclerView recyclerView =  findViewById(R.id.recycler_View);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));




        //add a toolbar to the xml- above the list
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // getting reference to the action bar, activate the homeclick, and make an icon.
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_drawer);

        /**
         * in this line we define what happen to the drawer, when an item will selected
         * in this case-the drawer will close.
         */
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                drawerLayout.closeDrawers();
                return false;
            }
        });

        coordinatorLayout = findViewById(R.id.coordinator);
        /**
         * in this block of code i wrote what happen when the user wants to add another item
         * to his shopping list.
         * he push on the floating button and a dialog pups and he can choose which item he wants.
         * @categoryList- refers to the list of the catogries that will be seen in the dialog.
         * @fab  -refer to the floating button.
         */
        categoryList = new ArrayList<>();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();// close the drawer if he open
                //build dialog and inflate in the dialog layout
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                final View view = getLayoutInflater().inflate(R.layout.new_product,null);
                catagoryTe = view.findViewById(R.id.new_category);
                final EditText product = view.findViewById(R.id.new_product);
                //creating an listview and listadapter that fit for the categories
                listView = view.findViewById(R.id.list_view);
                listAdapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,categoryList);
                listView.setAdapter(listAdapter);
                final String[] temp = new String[1];
                //define what happen when a user select category from the list
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String s ="";
                              s= s+  categoryList.get(position);
                              catagoryTe.setText(s);
                    }
                });
                Button addButton = view.findViewById(R.id.add_Button);
                addButton.setOnClickListener(new View.OnClickListener() {
                    /**
                     * in this block of code- we handle with the database, and store- hasemap.
                     * we check if the category already exists. if the category exists the product add
                     * to this category. and if not, make new category and add it to there.
                     */
                    @Override
                    public void onClick(View v) {
                        Category category = new Category(catagoryTe.getText().toString());
                        Set<Category> set = store.keySet();
                        boolean isExists = false;
                        int postion =0;
                        // checking if the category exists.
                        if (!category.getNameCategory().equals("")) {
                            for (Category c : set) {
                                if (c.equals(category)) {
                                    isExists = true;
                                    postion = getPositionFromdata(c,0);
                                    category = c;
                                    break;
                                }
                            }

                            if (isExists) { //exists
                                Product tempProduct = new Product(category, product.getText().toString());
                                category.add(tempProduct);
                                Log.d(TAG, "onClick: "+postion);
                                storeAdapter.notifyItemRangeChanged(0,storeAdapter.getItemCount());
                                //update the database
                                mychange=true;
                                db.collection("users").document(firebaseAuth.getUid()).
                                        collection("categories").document(category.getNameCategory()).set(category);

                            } else {// the category not exists in user list
                                categoryList.add(category.getNameCategory());
                                Product tempProduct = new Product(category, product.getText().toString());
                                category.add(tempProduct);
                                store.put(category, category.getProducts());
                                Log.d(TAG, "onClick: "+storeAdapter.getItemCount());
                                storeAdapter.notifyItemRangeChanged(0,storeAdapter.getItemCount()+1);
                                //update the database
                                mychange=true;
                                db.collection("users").document(firebaseAuth.getUid()).
                                        collection("categories").document(category.getNameCategory()).set(category);
                            }
                            //notifyDataSetChanged();// make the adapter to read the list all over again
                            Toast.makeText(MainActivity.this, "new product created", Toast.LENGTH_SHORT).show();
                            product.setText("");
                        }
                    }
                });

                builder.setView(view).setPositiveButton("finish", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Snackbar.make(coordinatorLayout, "items add", Snackbar.LENGTH_SHORT).show();
                    }
                }).show();

            }
        });

        store = new HashMap<>();
        /**
         * in this block we handle the registration/sign up and sign out.
         * those items-log in, sign up, sign out- exists in the drawer that pops when user slide or press the menu click
         * when item have been click- a dialog pups.
         * @registration- we make a new user.
         * @log in - we check if the user exists in the system, if he does, we download his list. if not- make a tost.
         * @log out- make a tost
         */
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                drawerLayout.closeDrawers();// close the window drawer
                //build a dialog that is fit for registration or sign up
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View view = getLayoutInflater().inflate(R.layout.sign_up_dialog,null);

                 final TextInputEditText username = view.findViewById(R.id.name_txt);
                final TextInputEditText email = view.findViewById(R.id.email_txt);
                final TextInputEditText password_text = view.findViewById(R.id.email_passwod);
                switch (menuItem.getItemId()) {

                    case R.id.Sign_Up:
                        builder.setView(view).setPositiveButton("Register", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                final String email_string  = email.getText().toString();
                                final String fullName = username.getText().toString();
                                final String password = password_text.getText().toString();

                                //sign up the user
                                firebaseAuth.createUserWithEmailAndPassword(email_string,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        if(task.isSuccessful()) {
                                            Map<String, Object> user = new HashMap<>();
                                            user.put("Email", email_string);
                                            user.put("FullName", fullName);
                                            user.put("Password", password);
                                            if (firebaseAuth.getUid() != null)
                                                db.collection("users").document(firebaseAuth.getUid());
                                            Snackbar.make(coordinatorLayout, "Sign up successful", Snackbar.LENGTH_SHORT).show();
                                        }
                                        else
                                            Snackbar.make(coordinatorLayout,"Sign up failed",Snackbar.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        }).show();
                        break;
                    case R.id.log_in:

                        username.setVisibility(View.GONE);
                        builder.setView(view).setPositiveButton("Login", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                String username  = email.getText().toString();
                                String password = password_text.getText().toString();

                                //Sign in the user

                                firebaseAuth.signInWithEmailAndPassword(username,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        if(task.isSuccessful())
                                            Snackbar.make(coordinatorLayout,"log in successful",Snackbar.LENGTH_SHORT).show();
                                        //here i want to load the products that he have in the list.
                                        else
                                            Snackbar.make(coordinatorLayout,"log in failed",Snackbar.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        }).show();
                        break;
                    case R.id.log_out:
                        firebaseAuth.signOut();
                        //here i want to save his shopping list before the user disconnected
                        break;
                }
                return false;
            }
        });




        storeAdapter = new StoreAdapter(store);
        /**
         *this block of code implements the listener in the adapter- that i define in the storeadapter class.
         *what happen when as been long click- deleted the item and if the category is empty he delete and also the category if category empty.
         *also we define what happen when user change the switch button.
         */
        storeAdapter.setMyAdapterListenr(new StoreAdapter.MyAdapterListenr() {
            /**
             * make changes in the boolean value- purchased or not.
             * @param c- category name
             * @param position- position in the category
             * @param view- which view is it- not use in here.
             * @param purchased- the boolean value
             */
             @Override
             public void boughtThisItem(Category c, int position, View view, boolean purchased) {
                 c.getProducts().get(position).setPurchased(purchased);
                 db.collection("users").document(firebaseAuth.getUid()).collection("categories")
                         .document(c.getNameCategory()).set(c);
                 mychange=true;

             }

            /**
             * this method- delete a items from the list of product.
             * we update the data in the hash-map, the adapter and the cloud database.
             * @param c- category name
             * @param position- position in the category.
             * @param view- whice view is it- not implemnt here
             */
             @Override
             public void onclick(Category c, int position, View view) {
                 //remove the product in the list
                store.get(c).remove(position);
                c.loseToIndexNUMber();
                //remove form the database the product
                db.collection("users").document(firebaseAuth.getUid()).collection("categories")
                        .document(c.getNameCategory()).set(c);
                if(c.getIndexNUMber() == 0){
                    //remove the category name if there is no product and also the last product
                    int postionInTheAdapter =getPositionFromdata(c,position);
                    storeAdapter.notifyItemRemoved(postionInTheAdapter);
                    storeAdapter.notifyItemRemoved(postionInTheAdapter-1);
                    store.remove(c);
                    //remove form the database the category
                    db.collection("users").document(firebaseAuth.getUid()).collection("categories")
                            .document(c.getNameCategory()).delete();
                }
                else {// remove only one product
                    int postionInTheAdapter = getPositionFromdata(c, position);
                    storeAdapter.notifyItemRemoved(postionInTheAdapter);
                }
                 mychange=true;
             }

            /**
             * this method retrieve the current item that the user press on.
             * @param postion- the full position in the recycle view
             */
            @Override
            public void currentPosition(int postion) {
                _currenPostion = postion;
                mychange=true;
            }
        });
        recyclerView.setAdapter(storeAdapter);


        /**
         *define what happen when the status of the user change.
         * if the user is login then the his list will be download.
         * that mean, if he leaves and return and didn't dis connect. next time that is load the app.
         * the list will be automatic will load to the system.
         */
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null){
                    if(fullName!=null){
                        user.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(fullName).build()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                fullName = null;
                                if(task.isSuccessful()){
                                    Snackbar.make(coordinatorLayout,user.getDisplayName()+" welcome!",Snackbar.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                    navigationView.getMenu().findItem(R.id.log_in).setVisible(false);
                    navigationView.getMenu().findItem(R.id.log_out).setVisible(true);
                    navigationView.getMenu().findItem(R.id.Sign_Up).setVisible(false);
                    if(firstTimeRunning) {
                    final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setMessage("Loading " + user.getDisplayName() + ", please wait...");
                    progressDialog.show();

                        final Handler handler = new Handler();
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                store.clear();

                                db.collection("users").document(firebaseAuth.getUid()).collection("categories")
                                        .orderBy("indexNUMber", Query.Direction.ASCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        Log.d(TAG, "onSuccess: " + queryDocumentSnapshots.size());
                                        for (QueryDocumentSnapshot x : queryDocumentSnapshots) {
                                            Log.d(TAG, "onSuccess: " + x.getId());
                                            categoryList.add(x.getId());
                                            Category category = x.toObject(Category.class);
                                            store.put(category, category.getProducts());
                                        }
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                storeAdapter.notifyDataSetChanged();
                                                progressDialog.dismiss();
                                            }
                                        });

                                    }
                                });
                            }
                        }.start();
                    }
                    //if(firstTimeRunning) {
                        Log.d(TAG, "onAuthStateChanged: reading here");
                        //tryToReachToFireBase();
                    //}



                }
                else {
                    navigationView.getMenu().findItem(R.id.log_in).setVisible(true);
                    navigationView.getMenu().findItem(R.id.log_out).setVisible(false);
                    navigationView.getMenu().findItem(R.id.Sign_Up).setVisible(true);
                    store.clear();
                    storeAdapter.notifyDataSetChanged();
                }
            }
        };
    }

    /**
     * tryToReachToFireBase method- connected to the cloud and fetch data from there.
     * is doing it by the wonderful api of the firebase.
     * when he fetch the data he transform it to category object and put it @store hash map
     */
    private void tryToReachToFireBase() {
        db.collection("users").document(firebaseAuth.getUid()).collection("categories").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.d(TAG, "onSuccess: "+queryDocumentSnapshots.size());
                for(QueryDocumentSnapshot x: queryDocumentSnapshots){
                    Log.d(TAG, "onSuccess: "+x.getId());
                    categoryList.add(x.getId());
                    Category category = x.toObject(Category.class);
                    store.put(category,category.getProducts());
                }
                storeAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * not on use system. use in older version.
     */
    private void connectToFireBae() {
        store.clear();
        //storeAdapter.notifyDataSetChanged();
        categoryInDataBase = new Category("meat");
        db.collection("users").document(firebaseAuth.getUid()).collection("categorys")//.get()
                .document("meat").collection("products").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Log.d(TAG, "onSuccess:" +queryDocumentSnapshots.size());
                        for (QueryDocumentSnapshot categoryDoc : queryDocumentSnapshots) {
                            //categoryInDataBase = new Category(categoryDoc.getId());
                            Product product = categoryDoc.toObject(Product.class);
                            categoryInDataBase.add(product);
                            Log.d(TAG, "onSuccess: "+product.getNameProduct());
                            Log.d(TAG, "onSuccess: "+categoryInDataBase.getNameCategory()+"  "+categoryInDataBase.indexNUMber);

                        }
                        store.put(categoryInDataBase, categoryInDataBase.getProducts());
                        storeAdapter.notifyDataSetChanged();
                    }

                });
    }

    /**
     *
     * @FirebaseUser- get a reference to the user id.
     * if user is log in- we will download is list from the system.
     */
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        //updateUI(currentUser);

        firebaseAuth.addAuthStateListener(authStateListener);

        db.collection("users/" + firebaseAuth.getUid() + "/categories").orderBy("indexNUMber", Query.Direction.ASCENDING).
                addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }
                        // older version of the algorithm - didn't work well.
                        if (!firstTimeRunning) {// if is first run is not download- because we implement it else where.
                            if (!mychange) {
                                Log.d(TAG, "onEvent: redind also here");
                                store.clear();
                                db.collection("users").document(firebaseAuth.getUid()).collection("categories")
                                        .orderBy("indexNUMber", Query.Direction.ASCENDING);
                                queryDocumentSnapshots.getQuery().orderBy("indexNUMber", Query.Direction.ASCENDING);
                                for (QueryDocumentSnapshot x : queryDocumentSnapshots) {
                                    //Log.d(TAG, "onSuccess: "+x.getId());//use for system checks
                                    Category category = x.toObject(Category.class);
                                    store.put(category, category.getProducts());
                                }
                                storeAdapter.notifyDataSetChanged();
                            }
                        }
                        firstTimeRunning = false;
                        mychange = false;

                    }
                });
    }
                    /*
                    addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                return;
                            }
                            /*
                            if(!firstTimeRunning) {
                                for (DocumentChange x : queryDocumentSnapshots.getDocumentChanges()) {
                                    Category c = (Category) x.getDocument().toObject(Category.class);
                                    Category ctemp =null;
                                    Set<Category> keySet = store.keySet();
                                    boolean exists = false;
                                    for(Category key:keySet) {
                                        if(key.equals(c)) {
                                            exists = true;
                                            ctemp = key;
                                            break;
                                        }
                                    }


                                    if (x.getType() == DocumentChange.Type.MODIFIED) {
                                        //check the arraylist if product added or removed.
                                        if(!((ctemp==null)||(ctemp==null))) {
                                            Log.d(TAG, "onEvent: "+x.getType().name());
                                            if (ctemp.getIndexNUMber() < c.getIndexNUMber()) {//add
                                                store.put(ctemp,c.getProducts());//ctemp.setProducts(c.products);
                                                ctemp.addToIndexNUMber();
                                                //storeAdapter.notifyDataSetChanged();
                                                //storeAdapter.notifyItemRangeInserted(0, storeAdapter.getItemCount());
                                            } else if (ctemp.getIndexNUMber() > c.getIndexNUMber()) {//removed
                                                store.put(ctemp,c.getProducts());//ctemp.setProducts(c.products);
                                                ctemp.loseToIndexNUMber();
                                                storeAdapter.notifyDataSetChanged();
                                                //storeAdapter.notifyItemRangeRemoved(0, storeAdapter.getItemCount());
                                                //storeAdapter.notifyItemRangeChanged(0,storeAdapter.getItemCount());
                                            }
                                            //else{
                                            if(mychange==false) {
                                                store.replace(ctemp, ctemp.getProducts(), c.getProducts());
                                                storeAdapter.notifyDataSetChanged();
                                            }
                                                //storeAdapter.notifyItemRangeChanged(0,storeAdapter.getItemCount());
                                            //}
                                        }
                                    }

                                    if (x.getType() == DocumentChange.Type.ADDED) {
                                        Log.d(TAG, "onEvent: "+x.getType().name());
                                        if (!exists) {
                                            Log.d(TAG, "onEvent1: "+storeAdapter.getItemCount());
                                            int temp =storeAdapter.getItemCount();
                                            store.put(c, c.getProducts());
                                            Log.d(TAG, "onEvent2: "+storeAdapter.getItemCount());
                                            storeAdapter.notifyItemRangeInserted(0, 2);
                                            //storeAdapter.notifyDataSetChanged();
                                            storeAdapter.notifyItemRangeChanged(0,storeAdapter.getItemCount());
                                        }
                                    }
                                    if (x.getType() == DocumentChange.Type.REMOVED) {
                                        Log.d(TAG, "onEvent: "+x.getType().name());
                                        if (exists) {
                                            //if(store.containsKey(ctemp))
                                               // Log.d(TAG, "onEvent: true");
                                            store.remove(ctemp);
                                            //storeAdapter.keys = store.keySet();
                                            storeAdapter.notifyDataSetChanged();
                                            //storeAdapter.notifyItemRangeRemoved(0, storeAdapter.getItemCount()-1);
                                        }
                                    }




                                    //Log.d(TAG, "onEvent: "+storeAdapter.getCategoryFromPosition(_currenPostion));
                                    //Category category = x.getDocument().toObject(Category.class);

                               }
                           }
                            firstTimeRunning = false;
                            mychange = false;
                            /*
                            // older version of the algorithm - didn't work well.
                            if (!firstTimeRunning) {// if is first run is not download- because we implement it else where.
                                if (!mychange) {
                                    Log.d(TAG, "onEvent: redind also here");
                                    store.clear();
                                    db.collection("users").document(firebaseAuth.getUid()).collection("categories")
                                            .orderBy("nameCategory", Query.Direction.ASCENDING);
                                    queryDocumentSnapshots.getQuery().orderBy("nameCategory", Query.Direction.ASCENDING);
                                    for (QueryDocumentSnapshot x : queryDocumentSnapshots) {
                                        //Log.d(TAG, "onSuccess: "+x.getId());//use for system checks
                                        Category category = x.toObject(Category.class);
                                        store.put(category, category.getProducts());
                                    }
                                    storeAdapter.notifyDataSetChanged();
                                }
                            }
                            firstTimeRunning = false;
                            mychange = false;

                        }
                    });
        Log.d(TAG, "onStart: "+"as been called");

    }*/

    @Override
    protected void onStop() {
        super.onStop();

        //disconnected from the cloud atu- avoid leaking of data
        firebaseAuth.removeAuthStateListener(authStateListener);



    }

    /**
     * because my data structure is not array- is hase map- and a list is one by one like array
     * i make changes to hash-map.
     * @param c- category selected
     * @param position- the position of the product in the category
     * @return the position of the product in adapter
     */
    private int getPositionFromdata(Category c, int position) {
        int index =1;
        Set<Category> keys = store.keySet();
        for(Category k: keys){
            if(c.nameCategory.equals(k.nameCategory)){
                index = index +position;
                break;
            }
            index = index+ k.getIndexNUMber()+1;
        }
        return index;
    }

    /**
     * open the drawer when a menu as been pushed
     * @param item- refers to the hamburger button.
     * @return boolean false because i don't now waht the system do with this.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            //Toast.makeText(this, "home sweet home", Toast.LENGTH_SHORT).show();
            drawerLayout.openDrawer(Gravity.RIGHT);
        }
        return super.onOptionsItemSelected(item);
    }
}
