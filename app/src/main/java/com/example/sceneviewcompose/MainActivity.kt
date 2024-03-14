package com.example.sceneviewcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.sceneviewcompose.ui.theme.SceneViewComposeTheme
import io.github.sceneview.Scene
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNode
import io.github.sceneview.rememberNodes

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SceneViewComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ModelSceneView()
                }
            }
        }
    }
}

@Composable
fun ModelSceneView() {
    Box(modifier = Modifier.fillMaxSize()) {
        val engine = rememberEngine()
        val modelLoader = rememberModelLoader(engine)
        val cameraNode = rememberCameraNode(engine).apply {
            position = Position(z = 4.0f)
        }
        val centerNode = rememberNode(engine)
            .addChildNode(cameraNode)
        var rotationState by remember {
            mutableStateOf(0f)
        }

        val rotationAnim by animateFloatAsState(
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )

        LaunchedEffect(rotationAnim) {
            rotationState += rotationAnim
        }
        Scene(
            modifier = Modifier.fillMaxSize(),
            engine = engine,
            modelLoader = modelLoader,
            cameraNode = cameraNode,
            childNodes = rememberNodes {
                add(ModelNode(modelLoader.createModelInstance("models/ramadan.glb")).apply {
                    // Move the node 4 units in Camera front direction
                    scaleToUnitCube(1.4f)
                })
            },
            onFrame = {
                cameraNode.lookAt(centerNode)
            }
        )
        Image(
            modifier = Modifier
                .width(140.dp)
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(16.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(
                        alpha = 0.5f
                    ),
                    shape = MaterialTheme.shapes.small
                )
                .padding(8.dp),
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo"
        )
    }
}