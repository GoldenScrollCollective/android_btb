package org.lemonadestand.btb.extensions

fun String.lastPathComponent() = substring(lastIndexOf("/")+1)