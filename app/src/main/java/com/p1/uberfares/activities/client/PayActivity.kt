package com.p1.uberfares.activities.client

import android.annotation.SuppressLint
import com.p1.uberfares.R
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PayActivity : AppCompatActivity() {
    private lateinit var payButton: Button
    private val lahzaApi: LahzaApi by lazy {
        RetrofitClient.instance.create(LahzaApi::class.java)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay)


        payButton = findViewById(R.id.payButton)
        payButton.setOnClickListener {
            initiatePayment()
        }

    }

    fun initiatePayment() {
        val email = "fares.shaher.fatooh@gmail.com" // استبدلها ببريد العميل الفعلي
        val mobile = "0599408420" // استبدلها برقم الهاتف الفعلي
        val amount = 20000.0 // استبدلها بالمبلغ الفعلي الذي يرغب العميل في دفعه

        val authorization =
            "Bearer " + "sk_test_bUSM8WVTjuWkfplgckYSDGaUJqvS1y5TU" // استبدل "YOUR_SECRET_KEY" بالمفتاح السري الخاص بك

        lahzaApi.initializeTransaction(authorization, email, mobile, amount)
            .enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.isSuccessful) {
                        val apiResponse = response.body()
                        if (apiResponse != null && apiResponse.status) {
                            // تمت عملية الدفع بنجاح
                            val reference = apiResponse.data?.reference
                            // اكمل هنا بتنفيذ الإجراءات الإضافية بعد الدفع الناجح
                        } else {
                            // معالجة الاستجابة الخاطئة هنا
                            val errorMessage = apiResponse?.message ?: "An error occurred."
                            // يمكنك استخدام الـ errorMessage لعرض رسالة خطأ للمستخدم أو تنفيذ أي إجراء آخر
                        }
                    } else {
                        // معالجة الاستجابة الخاطئة هنا
                        val errorMessage = response.message() // حصول على رسالة الخطأ من Retrofit
                        // يمكنك استخدام الـ errorMessage لعرض رسالة خطأ للمستخدم أو تنفيذ أي إجراء آخر

                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    // حدث خطأ في الاتصال
                }
            })

    }

}