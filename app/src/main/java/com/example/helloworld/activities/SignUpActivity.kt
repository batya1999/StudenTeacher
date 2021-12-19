package com.example.helloworld.activities
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import android.widget.Spinner
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.example.helloworld.R
import com.example.helloworld.firebase.FirestoreClass
import com.example.helloworld.models.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : BaseActivity() {

    /**
     * This function is auto created by Android when the Activity Class is created.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        //This call the parent constructor
        super.onCreate(savedInstanceState)
        // This is used to align the xml view to this class
        setContentView(R.layout.activity_sign_up)



        // This is used to hide the status bar and make the splash screen as a full screen activity.
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setupActionBar()

        val spn_area_R = findViewById<Spinner>(R.id.spn_area_R)


        val list: MutableList<String> = ArrayList()

        list.add("אזור")
        list.add("צפון")
        list.add("מרכז")
        list.add("יהודה  ושומרון")
        list.add("דרום")

        val adapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, list)
        spn_area_R.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val item = list[position]
                Toast.makeText(this@SignUpActivity, "$item selected", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


        spn_area_R.adapter = adapter
        limitDropDownHeight(spn_area_R)



        val spn_gender = findViewById<Spinner>(R.id.spn_gender)
        val list2: MutableList<String> = ArrayList()

        list2.add("מין")
        list2.add("זכר")
        list2.add("נקבה")
        list2.add("אחר")

        val adapter2 = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, list2)
        spn_gender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val item = list2[position]
                Toast.makeText(this@SignUpActivity, "$item selected", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


        spn_gender.adapter = adapter2
        limitDropDownHeight(spn_gender)


        // TODO (Step 11: Add a click event to the Sign-Up button and call the registerUser function.)
        // START
        // Click event for sign-up button.
        val btn_sign_up = findViewById<Button>(R.id.btn_sign_up)
        btn_sign_up.setOnClickListener {
            registerUser()
        }
//        val btn_i_want_teach = findViewById<Button>(R.id.btn_i_want_teach)
//        btn_i_want_teach.setOnClickListener {
//            val intent = Intent(this, WhatIAmTeaching::class.java)
//            startActivity(intent)
//        }
    }
    fun userRegisteredSuccess(){
        Toast.makeText(this@SignUpActivity, "you have successfully registered ",
            Toast.LENGTH_SHORT
        ).show()
        // Hide the progress dialog
        hideProgressDialog()
        /**
         * Here the new user registered is automatically signed-in so we just sign-out the user from firebase
         * and send him to Intro Screen for Sign-In
         */
        FirebaseAuth.getInstance().signOut()
        // Finish the Sign-Up Screen
        finish()
    }

    /**
     * A function for actionBar Setup.
     */
    private fun setupActionBar() {
//        val btn_teach = findViewById<Button>(R.id.btn_teach)
        val btn_sign_up = findViewById<Button>(R.id.btn_sign_up)
        setSupportActionBar(toolbar_sign_up_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        toolbar_sign_up_activity.setNavigationOnClickListener { onBackPressed() }

        btn_sign_up.setOnClickListener {
            registerUser()
        }
//        btn_i_want_teach.setOnClickListener {
//            val intent = Intent(this, WhatIAmTeaching::class.java)
//            startActivity(intent)
//        }
    }
    // TODO (Step 9: A function to register a new user to the app.)
    // START
    /**
     * A function to register a user to our app using the Firebase.
     * For more details visit: https://firebase.google.com/docs/auth/android/custom-auth
     */
    private fun registerUser() {
        val name: String = et_name.text.toString().trim { it <= ' ' }
        val email: String = et_email.text.toString().trim { it <= ' ' }
        val password: String = et_password.text.toString().trim { it <= ' ' }
        val phon: String = et_phon_num.text.toString().trim { it <= ' ' }
        val prof1: String = et_prof1.text.toString().trim { it <= ' ' }
        val prof2: String = et_prof2.text.toString().trim { it <= ' ' }
        val prof3: String = et_prof3.text.toString().trim { it <= ' ' }
        val Cpassword: String = et_confirm_password.text.toString().trim { it <= ' ' }

        if (validateForm(name, email, password, Cpassword,phon,prof1,prof2,prof3)) {
            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                    OnCompleteListener<AuthResult> { task ->



                        // If the registration is successfully done
                        if (task.isSuccessful) {

                            // Firebase registered user
                            val firebaseUser: FirebaseUser = task.result!!.user!!
                            // Registered Email
                            val registeredEmail = firebaseUser.email!!
                            val user = User(firebaseUser.uid, name, registeredEmail)
                            FirestoreClass().registerUser(this, user)
                        } else {
                            Toast.makeText(
                                this@SignUpActivity,
                                "Registration failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
        }
    }


    // END

    // TODO (Step 10: A function to validate the entries of a new user.)
    // START
    /**
     * A function to validate the entries of a new user.
     */
    private fun validateForm(name: String, email: String, password: String, cPsswoed: String, phon:String, prof1: String,prof2: String,prof3: String,): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                showErrorSnackBar("Please enter name.")
                false
            }
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter email.")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter password.")
                false
            }
            TextUtils.isEmpty(cPsswoed) -> {
                showErrorSnackBar("תאמת את הסיסמא שלך בבקשה")
                false
            }
            TextUtils.isEmpty(phon) -> {
                showErrorSnackBar("תכניס את המספר טלפון בבקשה")
                false
            }

            else -> {
                true
            }
        }
    }

    fun limitDropDownHeight(spnTest: Spinner) {
        val popup = Spinner::class.java.getDeclaredField("mPopup")
        popup.isAccessible = true

        val popupWindow: ListPopupWindow = popup.get(spnTest) as ListPopupWindow
        popupWindow.height = (200 * resources.displayMetrics.density).toInt()
    }

    fun append(arr: Array<TextView>, element: TextView): Array<TextView> {
        val list: MutableList<TextView> = arr.toMutableList()
        list.add(element)
        return list.toTypedArray()
        // END
    }
    private fun updateProfile() {
        // [START update_profile]
        val user = Firebase.auth.currentUser

        val profileUpdates = userProfileChangeRequest {
            displayName = "Jane Q. User"
            photoUri = Uri.parse("https://example.com/jane-q-user/profile.jpg")
        }

        user!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("successful", "User profile updated.")
                }
            }
        // [END update_profile]
    }
}