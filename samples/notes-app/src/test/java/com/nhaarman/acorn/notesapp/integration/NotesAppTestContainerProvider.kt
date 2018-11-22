/*
 * Acorn - Decoupling navigation from Android
 * Copyright (C) 2018 Niek Haarman
 *
 * Acorn is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Acorn is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Acorn.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.nhaarman.acorn.notesapp.integration

import com.nhaarman.acorn.notesapp.presentation.createitem.CreateItemScene
import com.nhaarman.acorn.notesapp.integration.presentation.createitem.TestCreateItemContainer
import com.nhaarman.acorn.notesapp.presentation.edititem.EditItemScene
import com.nhaarman.acorn.notesapp.integration.presentation.edititem.TestEditItemContainer
import com.nhaarman.acorn.notesapp.presentation.itemlist.ItemListScene
import com.nhaarman.acorn.notesapp.integration.presentation.itemlist.TestItemListContainer
import com.nhaarman.acorn.presentation.Container
import com.nhaarman.acorn.presentation.Scene
import com.nhaarman.acorn.testing.ContainerProvider

object NotesAppTestContainerProvider : ContainerProvider {

    override fun containerFor(scene: Scene<*>): Container {
        return when (scene) {
            is ItemListScene -> TestItemListContainer()
            is CreateItemScene -> TestCreateItemContainer()
            is EditItemScene -> TestEditItemContainer()
            else -> error("Unknown scene $scene")
        }
    }
}
