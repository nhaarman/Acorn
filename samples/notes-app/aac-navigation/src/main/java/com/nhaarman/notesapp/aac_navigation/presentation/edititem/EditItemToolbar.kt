package com.nhaarman.notesapp.aac_navigation.presentation.edititem

import android.content.Context
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import com.nhaarman.notesapp.aac_navigation.R

class EditItemToolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int = android.support.v7.appcompat.R.attr.toolbarStyle
) : Toolbar(context, attrs, defStyleAttr) {

    override fun onFinishInflate() {
        super.onFinishInflate()
        inflateMenu(R.menu.edititem_menu)
    }
}