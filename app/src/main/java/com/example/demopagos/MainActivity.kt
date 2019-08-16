package com.example.demopagos

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import io.card.payment.CardIOActivity
import io.card.payment.CreditCard
import kotlinx.android.synthetic.main.activity_main.*
import io.conekta.conektasdk.Conekta;
import io.conekta.conektasdk.Card;
import io.conekta.conektasdk.Token;
import org.json.JSONObject;
import android.R.attr.data
import com.example.demopagos.Objects.ResponsePagosOxxo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class MainActivity :AppCompatActivity(){
    private val SCAN_RESULT = 100
    var direcciones = ArrayList<String?>()
    internal lateinit var jsonAPI: ProfileUserServices
    //creamos el dispensador de servicios
    internal var compositeteDisposable : CompositeDisposable = CompositeDisposable()
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Coneckta
        Conekta.setPublicKey("key_BN3yZ8trwDxxzfE3ZTM7qqw")
        Conekta.collectDevice(this)

        //Escaneo de tarjeta
        btn_escaner_tarjeta.setOnClickListener {
            val scanIntent = Intent(this, CardIOActivity::class.java)
            scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true) // default: false
            scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false) // default: false
            scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false) // default: false
            startActivityForResult(scanIntent, SCAN_RESULT)
        }

        //Introduccion manual de la tarjeta
        //Numero de tarjeta
        Etx_numero_tarjeta.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(!Etx_numero_tarjeta.text.isEmpty()) {
                    if (Etx_numero_tarjeta.text.toString().substring(0, 1).equals("4")) {
                        Etx_numero_tarjeta.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                    } else if (Etx_numero_tarjeta.text.toString().substring(0, 1).equals("5")) {
                        Etx_numero_tarjeta.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                    }
                }else{
                    Etx_numero_tarjeta.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                }
            }
        })
        btn_pagar_oxxo.setOnClickListener {
            val retrofit = Client.instance
            jsonAPI = retrofit.create(ProfileUserServices::class.java)
            compositeteDisposable.add(jsonAPI.pagosOxxo(
                "Ropa",
                "1900",
                "10",
                "Roka",
                "rika_js@outlook.com",
                "1234567890"
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{response->ResponseData(response)}
            )
        }
        //Fecha de vencimiento
        Etx_ma_tarjeta.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Etx_ma_tarjeta.setOnKeyListener { v, keyCode, event ->
                    if(keyCode != KeyEvent.KEYCODE_DEL){
                        if(Etx_ma_tarjeta.text.length==2) {
                            val x = Etx_ma_tarjeta.text.toString() + "/"
                            Etx_ma_tarjeta.setText(x)
                            Etx_ma_tarjeta.setSelection(3)
                        }
                        false
                    }else{
                        false
                    }
                }

            }
        })



        btn_pasar_revision.setOnClickListener {
            if(!Etx_numero_tarjeta.text.isEmpty()&&!Etx_ma_tarjeta.text.isEmpty()&&!Etx_ccv_tarjeta.text.isEmpty()
                &&!Etx_nom_tarjeta.text.isEmpty()&&!Etx_direccion_tarjeta.text.isEmpty()&&Etx_numero_tarjeta.text.length==16){
                /*var intent = Intent (this, RevisarPedidoActivity::class.java)
                intent.putExtra("Numero",Etx_numero_tarjeta.text.toString())

                intent.putExtra("MM/AA",Etx_ma_tarjeta.text.toString())
                intent.putExtra("CCV",Etx_ccv_tarjeta.text.toString())
                intent.putExtra("Nombre",Etx_nom_tarjeta.text.toString())
                intent.putExtra("Direccion",Etx_direccion_tarjeta.text.toString())
                this.startActivity(intent)*/
               if(Etx_ma_tarjeta.text.toString().split("/")[0].matches("0[1-9]|1[0-2]".toRegex())){
                   val card : Card = Card(
                       Etx_nom_tarjeta.text.toString(),
                       Etx_numero_tarjeta.text.toString(),
                       Etx_ccv_tarjeta.text.toString(),
                       Etx_ma_tarjeta.text.toString().split("/")[0],
                       "20"+Etx_ma_tarjeta.text.toString().split("/")[1])
                   val token = Token(this)
                   token.onCreateTokenListener {
                       try {
                           //TODO: Create charge
                           //Log.d("Token::::", it.getString("id"))
                           if(it.getString("object").equals("token")){
                               val retrofit = Client.instance
                               jsonAPI = retrofit.create(ProfileUserServices::class.java)
                               compositeteDisposable.add(jsonAPI.pagosTarjeta(
                                   "Ropa",
                                   "1900",
                                   "10",
                                   "Roka",
                                   "rika_js@outlook.com",
                                   "1234567890",
                                   it.getString("id")
                               )
                                   .subscribeOn(Schedulers.io())
                                   .observeOn(AndroidSchedulers.mainThread())
                                   .subscribe{response->ResponseData(response)}
                               )
                           }else{
                               Toast.makeText(this,it.getString("message_to_purchaser"),Toast.LENGTH_SHORT).show()
                           }


                       } catch (err: Exception) {
                           //Log.d("Error:", err.toString())
                       }

                   }
                   token.create(card)
               }else{ Toast.makeText(this,"mes expiracion invalido",Toast.LENGTH_SHORT).show()
               }

            }else{
                Toast.makeText(this,"No debe de haber campos vacios",Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun ResponseData(response: ResponsePagosOxxo?) {
      when(response?.status){
          "1"->Toast.makeText(this,"Se realizo el pago",Toast.LENGTH_SHORT).show()
          "2"->Toast.makeText(this,response.error,Toast.LENGTH_SHORT).show()
      }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SCAN_RESULT) {
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                val scanResult = data.getParcelableExtra<CreditCard>(CardIOActivity.EXTRA_SCAN_RESULT)
                Etx_numero_tarjeta.setText(scanResult.cardNumber.toString())
                Log.d("Tarjeta",scanResult.cardNumber.toString().substring(0,1))
                if(scanResult.cardNumber.toString().substring(0,1).equals("4")){
                    Etx_numero_tarjeta.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
                }else if(scanResult.cardNumber.toString().substring(0,1).equals("5")){
                    Etx_numero_tarjeta.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
                }
                if(scanResult.isExpiryValid){
                    var x: String = ""
                    if(scanResult.expiryMonth.toString().length==1){
                        x="0"+scanResult.expiryMonth.toString()+'/'+scanResult.expiryYear.toString().substring(2,4)
                    }else{
                        x=scanResult.expiryMonth.toString()+'/'+scanResult.expiryYear.toString().substring(2,4)
                    }
                    Etx_ma_tarjeta.setText(x)

                }
            }
        }
    }

}