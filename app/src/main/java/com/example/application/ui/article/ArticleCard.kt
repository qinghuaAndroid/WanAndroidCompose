package com.example.application.ui.article

import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat
import coil.compose.AsyncImage
import com.example.application.bean.ArticleEntity
import com.example.common.R as CommonR
import com.example.core.R as CoreR

@Composable
fun ArticleCard(
    article: ArticleEntity.DatasBean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, colorResource(CommonR.color.division_line))
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (article.type == 1) {
                    Text(
                        text = "置顶",
                        color = colorResource(CommonR.color.red),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Text(
                    text = article.author.takeUnless { it.isNullOrBlank() }.orEmpty()
                        .ifBlank { article.shareUser.orEmpty() },
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = article.niceDate.orEmpty(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }

            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (!article.envelopePic.isNullOrBlank()) {
                    AsyncImage(
                        model = article.envelopePic,
                        contentDescription = null,
                        modifier = Modifier.size(width = 72.dp, height = 96.dp),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(CoreR.mipmap.ic_default_img),
                        error = painterResource(CoreR.mipmap.ic_default_img)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = article.title.fromHtml(),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 15.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = if (article.envelopePic.isNullOrBlank()) 2 else 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (!article.projectLink.isNullOrBlank() && !article.desc.isNullOrBlank()) {
                        Text(
                            text = article.desc.fromHtml(),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
            }

            Row(Modifier.fillMaxWidth().padding(top = 10.dp)) {
                Text(
                    text = article.superChapterName.orEmpty(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                if (!article.chapterName.isNullOrBlank()) {
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = article.chapterName.orEmpty(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

private fun String?.fromHtml(): String =
    HtmlCompat.fromHtml(orEmpty(), HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
