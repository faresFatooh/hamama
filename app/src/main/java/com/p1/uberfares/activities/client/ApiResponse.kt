package com.p1.uberfares.activities.client

data class ApiResponse(
    val status: Boolean,
    val message: String,
    val data: TransactionData? // إذا كان هناك بيانات إضافية تعود من واجهة API، احتمالية وجود البيانات هي optional (يمكن أن تكون قيمة null)
)

data class TransactionData(
    val reference: String,
    val transactionId: String,
    // قد تضيف هنا المزيد من البيانات الإضافية حسب استجابة واجهة API
)

