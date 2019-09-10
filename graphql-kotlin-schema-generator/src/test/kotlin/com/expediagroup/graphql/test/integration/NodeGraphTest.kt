/*
 * Copyright 2019 Expedia, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.expediagroup.graphql.test.integration

import com.expediagroup.graphql.TopLevelObject
import com.expediagroup.graphql.testSchemaConfig
import com.expediagroup.graphql.toSchema
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeReference
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class NodeGraphTest {

    @Test
    fun nodeGraph() {
        val queries = listOf(TopLevelObject(NodeQuery()))

        val schema = toSchema(queries = queries, config = testSchemaConfig)

        assertEquals(expected = 1, actual = schema.queryType.fieldDefinitions.size)
        assertEquals(expected = "nodeGraph", actual = schema.queryType.fieldDefinitions.first().name)

        val nodeFields = (schema.typeMap["Node"] as? GraphQLObjectType)?.fieldDefinitions

        nodeFields?.forEach {
            assertFalse(it.type is GraphQLTypeReference, "Node.${it.name} is a GraphQLTypeReference")
        }
    }
}

/**
 * Represents a recursive type that references itself,
 * similar to a tree node structure.
 */
data class Node(
    val id: Int,
    val value: String,
    val parent: Node? = null,
    var children: List<Node?> = emptyList()
)

class NodeQuery {

    private val root = Node(id = 0, value = "root")
    private val nodeA = Node(id = 1, value = "A", parent = root)
    private val nodeB = Node(id = 2, value = "B", parent = root)
    private val nodeC = Node(id = 3, value = "C", parent = nodeB)

    init {
        root.children = listOf(nodeA, nodeB)
        nodeB.children = listOf(nodeC)
    }

    fun nodeGraph() = root
}
