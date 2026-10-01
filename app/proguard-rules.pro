# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.SerializationKt
-keepclassmembers class * {
    *** Companion;
}
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,allowobfuscation,allowoptimization class * {
    @kotlinx.serialization.Serializable class *;
}
-keepclassmembers class com.example.taras.network_calls.taras.model.** {
    <fields>;
}

# Room Database & DAOs
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Retrofit, OkHttp, Okio
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**
