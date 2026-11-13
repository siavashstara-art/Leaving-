package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.model.AppLanguage
import com.example.model.ContentCategory
import com.example.ui.theme.LocalHonarColors

fun Int.toLocalizedDigits(language: AppLanguage): String {
  val raw = this.toString()
  if (language != AppLanguage.PERSIAN) return raw
  val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
  return buildString(raw.length) {
    for (ch in raw) {
      if (ch in '0'..'9') append(persianDigits[ch - '0']) else append(ch)
    }
  }
}

@Composable
fun EditorialReadingColumn(
  modifier: Modifier = Modifier,
  content: @Composable BoxScope.() -> Unit,
) {
  Box(
    modifier = modifier.fillMaxWidth(),
    contentAlignment = Alignment.TopCenter,
  ) {
    Box(modifier = Modifier.fillMaxWidth().widthIn(max = 640.dp), content = content)
  }
}

@Composable
fun LocationBreadcrumbBar(
  trail: String,
  modifier: Modifier = Modifier,
) {
  Surface(
    modifier = modifier.fillMaxWidth(),
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
  ) {
    Text(
      text = trail,
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp),
    )
  }
}

@Composable
fun CategoryBadge(
  category: ContentCategory,
  language: AppLanguage,
  modifier: Modifier = Modifier,
) {
  val extra = LocalHonarColors.current
  val accent = extra.accentFor(category)
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(3.dp),
    color = extra.containerFor(category),
    border = BorderStroke(0.75.dp, accent.copy(alpha = if (extra.highContrast) 0.95f else 0.4f)),
  ) {
    Text(
      text = if (language == AppLanguage.PERSIAN) category.titleFa else category.titleEn,
      style = MaterialTheme.typography.labelSmall,
      color = accent,
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
    )
  }
}

@Composable
fun OrnamentalGoldDivider(modifier: Modifier = Modifier) {
  val gold = LocalHonarColors.current.goldAccent
  Row(
    modifier = modifier.fillMaxWidth().padding(vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center,
  ) {
    HorizontalDivider(
      modifier = Modifier.weight(1f),
      thickness = 0.75.dp,
      color = gold.copy(alpha = 0.35f),
    )
    Text(
      text = " ❖ ",
      color = gold,
      style = MaterialTheme.typography.labelSmall,
      modifier = Modifier.padding(horizontal = 8.dp),
    )
    HorizontalDivider(
      modifier = Modifier.weight(1f),
      thickness = 0.75.dp,
      color = gold.copy(alpha = 0.35f),
    )
  }
}

@Composable
fun FriendlyEmptyState(
  title: String,
  message: String,
  modifier: Modifier = Modifier,
) {
  val extra = LocalHonarColors.current
  Surface(
    modifier = modifier.fillMaxWidth().semantics(mergeDescendants = true) {},
    shape = MaterialTheme.shapes.medium,
    color = MaterialTheme.colorScheme.surface,
    border = BorderStroke(0.75.dp, extra.goldAccent.copy(alpha = 0.35f)),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(28.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Icon(
        imageVector = Icons.Outlined.AutoStories,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(36.dp),
      )
      Spacer(modifier = Modifier.height(12.dp))
      Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
      )
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = message,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
      )
    }
  }
}

@Composable
fun SectionHeaderRow(
  title: String,
  subtitle: String? = null,
  modifier: Modifier = Modifier,
  action: (@Composable () -> Unit)? = null,
) {
  Row(
    modifier = modifier.fillMaxWidth().padding(vertical = 4.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.onBackground,
      )
      if (!subtitle.isNullOrBlank()) {
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }
    if (action != null) {
      Spacer(modifier = Modifier.width(8.dp))
      Box { action() }
    }
  }
}
