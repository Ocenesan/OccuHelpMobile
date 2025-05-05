package com.example.occuhelpcompose.updatedpassword

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.occuhelpcompose.R
import com.example.occuhelpcompose.ui.theme.OccuHelpComposeTheme
import com.example.occuhelpcompose.ui.theme.PrimaryText

class UpdatedPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OccuHelpComposeTheme {
                UpdatedPassword()
            }
        }
    }
}

@Composable
fun UpdatedPassword(modifier: Modifier = Modifier) {
    ConstraintLayout(
        modifier = modifier.fillMaxSize()
    ) {
        val (backButton, titleText, subTitleText, imageCheck, buttonLogin,
            guideline1, guideline2, guideline3, guideline4) = createRefs()

        val topGuideline = createGuidelineFromTop(0.03f)
        val startGuideline = createGuidelineFromStart(0.05f)
        val endGuideline = createGuidelineFromEnd(0.05f)
        val bottomGuideline = createGuidelineFromBottom(0.20f)

        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.Red)
                .constrainAs(guideline1) {
                    top.linkTo(topGuideline)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )
        Box(
            modifier = modifier
                .fillMaxHeight()
                .width(1.dp)
                .background(Color.Red)
                .constrainAs(guideline2) {
                    start.linkTo(startGuideline)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
        )
        Box(
            modifier = modifier
                .fillMaxHeight()
                .width(1.dp)
                .background(Color.Red)
                .constrainAs(guideline3) {
                    end.linkTo(endGuideline)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
        )
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.Red)
                .constrainAs(guideline4) {
                    bottom.linkTo(bottomGuideline)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        IconButton(
            onClick = { /*TODO*/ },
            modifier = modifier
                .constrainAs(backButton) {
                    top.linkTo(topGuideline)
                    start.linkTo(startGuideline)
                }
                .size(32.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back Button",
                Modifier.size(32.dp)
            )
        }

        Text(
            text = "Kata Sandi Diperbarui!",
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp,
            lineHeight = 50.sp,
            color = PrimaryText,
            modifier = modifier.constrainAs(titleText) {
                top.linkTo(backButton.bottom, margin = 16.dp)
                start.linkTo(startGuideline)
                end.linkTo(endGuideline)
                width = Dimension.fillToConstraints
                height = Dimension.wrapContent
            }
        )

        Text(
            text = "Kata sandi Anda berhasil diperbarui. \n" +
                    "Silahkan Login kembali.",
            color = Color(0xFF185C6D),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 14.sp,
            modifier = modifier.constrainAs(subTitleText) {
                top.linkTo(titleText.bottom, margin = 16.dp)
                start.linkTo(startGuideline)
                end.linkTo(endGuideline)
                width = Dimension.fillToConstraints
                height = Dimension.wrapContent
            }
        )

        Image(
            imageVector = Icons.Default.CheckCircleOutline,
            contentDescription = "Icon Check",
            modifier = modifier.size(200.dp).constrainAs(imageCheck) {
                start.linkTo(startGuideline)
                end.linkTo(endGuideline)
                top.linkTo(subTitleText.bottom, margin = 32.dp)
                width = Dimension.fillToConstraints
                height = Dimension.wrapContent
            },
            colorFilter = ColorFilter.tint(PrimaryText)
        )

        OutlinedButton(
            onClick = { /*TODO*/ },
            border = BorderStroke(1.dp, PrimaryText),
            shape = RoundedCornerShape(8.dp),
            modifier = modifier.constrainAs(buttonLogin) {
                bottom.linkTo(bottomGuideline)
                end.linkTo(endGuideline)
                start.linkTo(startGuideline)
            }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = modifier,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.vector_masuk),
                    contentDescription ="Icon Login",
                    modifier.size(18.dp)
                )
                Text(text = "Masuk", fontSize = 18.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UpdatedPasswordReview() {
    OccuHelpComposeTheme {
        UpdatedPassword()
    }
}