package org.example.w06_chapter12.code.code1

class AddUseCase {
    fun add(vararg args: Int): Int {
        return args.sum()
    }
}