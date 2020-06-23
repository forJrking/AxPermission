-keep @com.forjrking.permission.annotation.* class * {*;}
-keep class * {
    @com.forjrking.permission.annotation.* <fields>;
}
-keepclassmembers class * {
    @com.forjrking.permission.annotation.* <methods>;
}