package com.nhaarman.notesapp.conductor.presentation.itemlist

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.rxlifecycle2.ControllerEvent
import com.bluelinelabs.conductor.rxlifecycle2.RxController
import com.jakewharton.rxbinding2.view.clicks
import com.nhaarman.notesapp.conductor.R
import com.nhaarman.notesapp.conductor.noteAppComponent
import com.nhaarman.notesapp.conductor.presentation.createitem.CreateItemController
import com.nhaarman.notesapp.conductor.presentation.edititem.EditItemController
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.controller_itemlist.view.*

class ItemsListController : RxController() {

    private var disposable: Disposable? = null
        set(value) {
            field?.dispose()
            field = value
        }

    private val noteItemsRepository get() = activity!!.noteAppComponent.noteItemsRepository

    private val items by lazy {
        noteItemsRepository
            .noteItems
            .observeOn(AndroidSchedulers.mainThread())
            .replay(1)
            .autoConnect(1) { disposable ->
                if (isDestroyed) {
                    disposable.dispose()
                } else {
                    this.disposable = disposable
                }
            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.controller_itemlist, container, false)
    }

    @SuppressLint("CheckResult")
    override fun onAttach(view: View) {
        items
            .compose(bindUntilEvent(ControllerEvent.DETACH))
            .subscribe {
                view.itemsRecyclerView.items = it
            }

        view.createButton
            .clicks()
            .compose(bindUntilEvent(ControllerEvent.DETACH))
            .subscribe {
                router.pushController(RouterTransaction.with(CreateItemController()))
            }

        view.itemsRecyclerView
            .itemClicks
            .compose(bindUntilEvent(ControllerEvent.DETACH))
            .subscribe {
                router.pushController(RouterTransaction.with(EditItemController(it.id)))
            }

        view.itemsRecyclerView
            .deleteClicks
            .compose(bindUntilEvent(ControllerEvent.DETACH))
            .subscribe {
                noteItemsRepository.delete(it)
            }
    }

    override fun onDestroy() {
        disposable = null
    }
}