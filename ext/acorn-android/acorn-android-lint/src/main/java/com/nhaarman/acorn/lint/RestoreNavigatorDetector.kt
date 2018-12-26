/*
 *    Copyright 2018 Niek Haarman
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nhaarman.acorn.lint

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Context
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiType
import com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UMethod
import org.jetbrains.uast.getContainingUClass
import org.jetbrains.uast.visitor.AbstractUastVisitor

class RestoreNavigatorDetector : Detector(), Detector.UastScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>>? {
        return listOf(
            UMethod::class.java,
            UCallExpression::class.java
        )
    }

    private val constructedNavigators = mutableSetOf<ConstructedNavigator>()
    private val referencedNavigators = mutableSetOf<PsiType>()

    private fun PsiClass.allSupers(): List<PsiClass> {
        return supers
            .flatMap {
                it.allSupers() + it
            }
            .distinct()
    }

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return object : UElementHandler() {

            val navigatorType = PsiType.getTypeByName(
                "com.nhaarman.acorn.navigation.Navigator",
                context.uastContext.project,
                GlobalSearchScope.everythingScope(context.uastContext.project)
            )

            private val supportedNavigatorFQNs = listOf(
                "com.nhaarman.acorn.navigation.CompositeStackNavigator",
                "com.nhaarman.acorn.navigation.CompositeReplacingNavigator"
            )

            override fun visitCallExpression(node: UCallExpression) {
                val containingClass = node.getContainingUClass() ?: return
                if (containingClass.allSupers().none { it.qualifiedName in supportedNavigatorFQNs }) {
                    return
                }

                val returnType = node.returnType ?: return
                if (returnType == navigatorType) {
                    return
                }

                val isNavigator = navigatorType.isAssignableFrom(returnType)
                if (!isNavigator) {
                    return
                }

                constructedNavigators += ConstructedNavigator(returnType, node)
            }

            override fun visitMethod(node: UMethod) {
                if (node.name != "instantiateNavigator") return

                val body = node.uastBody ?: return
                body.accept(object : AbstractUastVisitor() {

                    override fun visitCallExpression(node: UCallExpression): Boolean {
                        val type = node.returnType ?: return false

                        val isNavigator = navigatorType.isAssignableFrom(type)
                        if (!isNavigator) return false

                        referencedNavigators += type

                        return true
                    }
                })
            }
        }
    }

    override fun afterCheckFile(context: Context) {
        constructedNavigators
            .filter { it.type !in referencedNavigators }
            .forEach {
                (context as JavaContext).report(
                    RestoreNavigatorDetector.issue,
                    it.element,
                    context.getNameLocation(it.element),
                    "Navigator is not restored"
                )
            }

        constructedNavigators.clear()
        referencedNavigators.clear()
    }

    private data class ConstructedNavigator(val type: PsiType, val element: UElement)

    companion object {

        val issue = Issue.create(
            "Acorn_RestoreNavigator",
            "Navigator created but not restored",
            "Sub Navigators used in this Navigator should be restored in instantiateNavigator.",
            Category.CORRECTNESS,
            5,
            Severity.ERROR,
            Implementation(
                RestoreNavigatorDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }
}