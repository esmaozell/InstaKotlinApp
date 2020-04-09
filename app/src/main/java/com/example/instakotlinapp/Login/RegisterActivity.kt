package com.example.instakotlinapp.Login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.example.instakotlinapp.Models.Users
import com.example.instakotlinapp.R
import com.example.instakotlinapp.utils.EventbusDataEvents
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_register.*
import org.greenrobot.eventbus.EventBus

class RegisterActivity : AppCompatActivity(),FragmentManager.OnBackStackChangedListener  {
    lateinit var manager :FragmentManager
    lateinit var  mRef :DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mRef  =FirebaseDatabase.getInstance().reference

        manager=supportFragmentManager
        manager.addOnBackStackChangedListener (this)
        init()
    }

    private fun init() {
        tvEposta.setOnClickListener {
            viewTelefon.visibility=View.INVISIBLE
            viewEposta.visibility=View.VISIBLE
            etGirisYontemi.setText("")
            etGirisYontemi.inputType=InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            etGirisYontemi.setHint("E-Posta")
            btnIleri.isEnabled=false
        }
        tvTelefon.setOnClickListener {
            viewTelefon.visibility=View.VISIBLE
            viewEposta.visibility=View.INVISIBLE
            etGirisYontemi.setText("")
            etGirisYontemi.inputType=InputType.TYPE_CLASS_PHONE
            etGirisYontemi.setHint("Telefon")
            btnIleri.isEnabled=false
        }

        etGirisYontemi.addTextChangedListener (object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.length >= 10){
                    btnIleri.isEnabled=true
                    btnIleri.setTextColor(ContextCompat.getColor(this@RegisterActivity,R.color.beyaz))
                    btnIleri.setBackgroundResource(R.drawable.register_button_aktif)
                }
                else{

                    btnIleri.isEnabled=false
                    btnIleri.setTextColor(ContextCompat.getColor(this@RegisterActivity,R.color.sonukmavi))
                    btnIleri.setBackgroundResource(R.drawable.register_button)


                }
            }


        })

        btnIleri.setOnClickListener{
            if(etGirisYontemi.hint.toString().equals("Telefon")){
                var ceptelefonuKullanimdaMi=false

                if (isValidTelefon(etGirisYontemi.text.toString())){
                    mRef.child("users").addListenerForSingleValueEvent(object  :ValueEventListener{
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            if(p0!!.getValue() != null){
                                for (user in p0!!.children){
                                    var okunanKullanici= user.getValue(Users::class.java)
                                    if(okunanKullanici!!.phone_number!!.equals(etGirisYontemi.text.toString())){
                                        Toast.makeText(this@RegisterActivity,"Telefon numarası  kullanimda",Toast.LENGTH_SHORT).show()
                                        ceptelefonuKullanimdaMi=true
                                        break
                                    }
                                }
                                if (ceptelefonuKullanimdaMi == false){
                                    loginRoot.visibility=View.GONE
                                    loginContainer.visibility=View.VISIBLE
                                    var transaction=supportFragmentManager.beginTransaction()
                                    transaction.replace(R.id.loginContainer,TelefonKoduGirFragment())
                                    transaction.addToBackStack("telefonKoduGirFragmentEklendi")
                                    transaction.commit()
                                    EventBus.getDefault().postSticky(EventbusDataEvents.KayitBilgileriniGonder(etGirisYontemi.text.toString(),null,null,null,false))


                                }
                            }
                        }


                    })

                      }else{
                    Toast.makeText(this,"Lütfen geçerli bir telefon numarası giriniz",Toast.LENGTH_SHORT).show()
                }
            }

            else{
                if (isValidEmail(etGirisYontemi.text.toString())){

                    var emailKullanimdaMi = false

                    mRef.child("users").addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            if(p0!!.getValue() != null){
                                for(user in p0!!.children){
                                    var okunanKullanici=user.getValue(Users::class.java)
                                    if (okunanKullanici?.email.equals(etGirisYontemi.text.toString())){
                                        Toast.makeText(this@RegisterActivity,"Email kullanımda",Toast.LENGTH_SHORT).show()
                                        emailKullanimdaMi=true
                                        break
                                    }
                                }

                                if(emailKullanimdaMi == false){
                                    loginRoot.visibility=View.GONE
                                    loginContainer.visibility=View.VISIBLE
                                    var transaction=supportFragmentManager.beginTransaction()
                                    transaction.replace(R.id.loginContainer,KayitFragment())
                                    transaction.addToBackStack("EmailGirisYontemiFragmentEklendi")
                                    transaction.commit()
                                    EventBus.getDefault().postSticky(EventbusDataEvents.KayitBilgileriniGonder(null,etGirisYontemi.text.toString(),null,null,true))


                                }
                            }
                        }

                    })

                    }
                else{
                    Toast.makeText(this,"Lütfen geçerli bir email  giriniz",Toast.LENGTH_SHORT).show()
                }

            }
        }

    }
    override fun onBackStackChanged() {
        val elemanSayisi = manager.backStackEntryCount
        if(elemanSayisi==0){
            loginRoot.visibility =View.VISIBLE
        }
    }
    fun isValidEmail(kontrolEdilecekMail :String) :Boolean{
        if(kontrolEdilecekMail == null){
            return false
        }
        return android.util.Patterns.EMAIL_ADDRESS.matcher(kontrolEdilecekMail).matches()
    }

    fun isValidTelefon(kontrolEdilecekTelefon :String) :Boolean{
        if(kontrolEdilecekTelefon == null || kontrolEdilecekTelefon.length >14 ){
            return false
        }
        return android.util.Patterns.PHONE.matcher(kontrolEdilecekTelefon).matches()
    }
}
