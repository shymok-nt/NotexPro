package com.example.kalemlik

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass

class NotebookViewModel : ViewModel() {
    val notebooks: List<Notebook> = emptyList()
    var pendingNotebookName by mutableStateOf("")
        private set
    var pendingCoverIndex by mutableIntStateOf(0)
        private set

    fun saveNotebook(name: String, coverIndex: Int) {
        pendingNotebookName = name.trim()
        pendingCoverIndex = coverIndex
    }
}

data class Notebook(
    val id: String,
    val name: String,
    val coverIndex: Int,
    val pageCount: Int
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KalemlikTheme {
                val sizeClass = calculateWindowSizeClass(this)
                KalemlikApp(sizeClass.widthSizeClass)
            }
        }
    }
}

@Composable
fun KalemlikApp(widthClass: WindowWidthSizeClass) {
    val navController = rememberNavController()
    val vm: NotebookViewModel = viewModel()
    NavHost(navController, startDestination = "library") {
        composable("library") { LibraryScreen(navController, widthClass, vm) }
        composable("editor?name={name}&cover={cover}", arguments = listOf(navArgument("name") { type = NavType.StringType; defaultValue = "" }, navArgument("cover") { type = NavType.IntType; defaultValue = 0 })) { entry ->
            EditorScreen(navController, widthClass, vm, entry.arguments?.getString("name").orEmpty(), entry.arguments?.getInt("cover") ?: 0)
        }
        composable("settings") { SettingsScreen(navController, widthClass) }
    }
}

@Composable
private fun LibraryScreen(nav: NavHostController, widthClass: WindowWidthSizeClass, vm: NotebookViewModel) {
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var selected by rememberSaveable { mutableStateOf("all") }
    val tablet = widthClass != WindowWidthSizeClass.Compact
    if (tablet) {
        Row(Modifier.fillMaxSize().background(KalemlikTokens.Background)) {
            LibrarySidebar(selected, { selected = it }, nav)
            LibraryContent(true, { showDialog = true }, notebooks = vm.notebooks)
        }
    } else {
        Scaffold(
            containerColor = KalemlikTokens.Background,
            bottomBar = { MobileBottomBar(selected, { selected = it }, nav) },
            floatingActionButton = {
                FloatingActionButton(onClick = { showDialog = true }, containerColor = KalemlikTokens.Accent, contentColor = Color.White) {
                    Icon(Icons.Default.Add, stringResource(R.string.new_notebook))
                }
            }
        ) { padding ->
            LibraryContent(false, { showDialog = true }, Modifier.padding(padding), notebooks = vm.notebooks)
        }
    }
    if (showDialog) {
        NewNotebookDialog(
            onDismiss = { showDialog = false },
            onSave = { name, cover ->
                vm.saveNotebook(name, cover)
                showDialog = false
                nav.navigate("editor?name=${android.net.Uri.encode(name)}&cover=$cover")
            }
        )
    }
}

@Composable
private fun LibrarySidebar(selected: String, onSelected: (String) -> Unit, nav: NavHostController) {
    Column(Modifier.width(250.dp).fillMaxHeight().background(KalemlikTokens.Navigation).padding(18.dp)) {
        Text(stringResource(R.string.app_name), fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 22.dp))
        listOf("all" to R.string.all_notebooks, "favorites" to R.string.favorites, "recent" to R.string.recent).forEach { (key, label) ->
            SidebarItem(stringResource(label), key == selected, Icons.Default.GridView) { onSelected(key) }
        }
        Text(stringResource(R.string.folders).uppercase(), color = KalemlikTokens.SectionText, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 26.dp, bottom = 8.dp))
        SidebarItem(stringResource(R.string.add_folder), false, Icons.Default.Folder) {}
        Text(stringResource(R.string.tags).uppercase(), color = KalemlikTokens.SectionText, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 20.dp, bottom = 8.dp))
        SidebarItem(stringResource(R.string.add_tag), false, Icons.Default.Layers) {}
        Spacer(Modifier.weight(1f))
        SidebarItem(stringResource(R.string.trash), false, Icons.Default.Delete) {}
        SidebarItem(stringResource(R.string.settings), false, Icons.Default.Settings) { nav.navigate("settings") }
    }
}

@Composable
private fun SidebarItem(label: String, active: Boolean, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().height(44.dp).clip(RoundedCornerShape(10.dp)).background(if (active) KalemlikTokens.Surface else Color.Transparent).clickable(onClick = onClick).padding(horizontal = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, label, tint = if (active) Color.White else KalemlikTokens.SecondaryText, modifier = Modifier.size(19.dp))
        Text(label, color = if (active) Color.White else KalemlikTokens.SecondaryText, fontSize = 14.sp, modifier = Modifier.padding(start = 12.dp))
    }
}

@Composable
private fun LibraryContent(tablet: Boolean, onNew: () -> Unit, modifier: Modifier = Modifier, notebooks: List<Notebook>) {
    Column(modifier.fillMaxSize().padding(horizontal = if (tablet) 36.dp else 20.dp, vertical = if (tablet) 28.dp else 18.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.notebooks), fontSize = if (tablet) 28.sp else 25.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            if (tablet) {
                SearchField()
                Spacer(Modifier.width(12.dp))
                OutlinedButton(onClick = {}, shape = RoundedCornerShape(KalemlikTokens.ButtonCorner)) { Icon(Icons.Default.PictureAsPdf, stringResource(R.string.import_pdf)); Spacer(Modifier.width(6.dp)); Text(stringResource(R.string.import_pdf)) }
                Spacer(Modifier.width(10.dp))
                Button(onClick = onNew, shape = RoundedCornerShape(KalemlikTokens.ButtonCorner)) { Icon(Icons.Default.Add, stringResource(R.string.new_notebook)); Spacer(Modifier.width(5.dp)); Text(stringResource(R.string.new_notebook)) }
            } else {
                IconButton(onClick = {}) { Icon(Icons.Default.Search, stringResource(R.string.search)) }
            }
        }
        if (notebooks.isEmpty()) EmptyLibrary() else NotebookGrid(notebooks)
    }
}

@Composable
private fun NotebookGrid(notebooks: List<Notebook>) {
    LazyColumn(Modifier.fillMaxSize().padding(top = 28.dp)) {
        items(notebooks) { notebook ->
            Row(Modifier.fillMaxWidth().padding(bottom = 18.dp)) {
                Box(Modifier.width(180.dp).height(200.dp).background(KalemlikTokens.CoverPalette[notebook.coverIndex], RoundedCornerShape(14.dp))) {
                    Text(notebook.name, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.BottomStart).padding(14.dp))
                }
                Column(Modifier.padding(start = 14.dp)) {
                    Text(notebook.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Text(stringResource(R.string.page_count, notebook.pageCount), color = KalemlikTokens.SecondaryText, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun SearchField() {
    Row(Modifier.width(260.dp).height(44.dp).clip(CircleShape).background(KalemlikTokens.Surface).padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Search, stringResource(R.string.search), tint = KalemlikTokens.SecondaryText, modifier = Modifier.size(20.dp))
        Text(stringResource(R.string.search_notebooks), color = KalemlikTokens.SecondaryText, fontSize = 13.sp, modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
private fun EmptyLibrary() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Edit, null, tint = KalemlikTokens.SecondaryText, modifier = Modifier.size(48.dp))
            Text(stringResource(R.string.no_notebooks), fontSize = 18.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 12.dp))
            Text(stringResource(R.string.create_first_notebook), color = KalemlikTokens.SecondaryText, fontSize = 14.sp, modifier = Modifier.padding(top = 5.dp))
        }
    }
}

@Composable
private fun MobileBottomBar(selected: String, onSelected: (String) -> Unit, nav: NavHostController) {
    Row(Modifier.fillMaxWidth().background(KalemlikTokens.Navigation).padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
        listOf("all" to R.string.notebooks, "favorites" to R.string.favorites, "folders" to R.string.folders, "settings" to R.string.settings).forEach { (key, label) ->
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { if (key == "settings") nav.navigate("settings") else onSelected(key) }.padding(horizontal = 9.dp)) {
                Icon(if (key == "settings") Icons.Default.Settings else if (key == "folders") Icons.Default.Folder else if (key == "favorites") Icons.Default.FavoriteBorder else Icons.Default.GridView, stringResource(label), tint = if (selected == key) Color.White else KalemlikTokens.SecondaryText)
                Text(stringResource(label), color = if (selected == key) Color.White else KalemlikTokens.SecondaryText, fontSize = 11.sp)
            }
        }

        @Composable
        private fun NewNotebookDialog(onDismiss: () -> Unit, onSave: (String, Int) -> Unit) {
            var name by rememberSaveable { mutableStateOf("") }
            var cover by rememberSaveable { mutableIntStateOf(0) }
            var template by rememberSaveable { mutableIntStateOf(0) }
            var pageSize by rememberSaveable { mutableIntStateOf(0) }
            AlertDialog(
                onDismissRequest = onDismiss,
                containerColor = KalemlikTokens.Surface,
                title = { Text(stringResource(R.string.new_notebook), fontWeight = FontWeight.Bold) },
                text = {
                    Column(Modifier.fillMaxWidth()) {
                        BasicTextField(
                            value = name,
                            onValueChange = { name = it },
                            singleLine = true,
                            textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
                            modifier = Modifier.fillMaxWidth().background(KalemlikTokens.Background, RoundedCornerShape(10.dp)).padding(14.dp),
                            decorationBox = { inner -> if (name.isEmpty()) Text(stringResource(R.string.notebook_name_hint), color = KalemlikTokens.SecondaryText); inner() }
                        )
                        ChoiceRow(stringResource(R.string.cover_color), KalemlikTokens.CoverPalette, cover) { cover = it }
                        ChoiceRow(stringResource(R.string.page_template), listOf(KalemlikTokens.Paper, KalemlikTokens.PaperLine, KalemlikTokens.Divider, KalemlikTokens.SecondaryText), template) { template = it }
                        Text(stringResource(R.string.page_size), color = KalemlikTokens.SecondaryText, fontSize = 12.sp, modifier = Modifier.padding(top = 12.dp))
                        Row(Modifier.horizontalScroll(rememberScrollState()).padding(top = 7.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(R.string.a4, R.string.a5, R.string.portrait_screen).forEachIndexed { index, label ->
                                FilterChip(stringResource(label), pageSize == index) { pageSize = index }
                            }
                        }
                    }
                },
                confirmButton = { Button(onClick = { onSave(name, cover) }, enabled = name.isNotBlank(), shape = RoundedCornerShape(KalemlikTokens.ButtonCorner)) { Text(stringResource(R.string.save)) } },
                dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } }
            )
        }

        @Composable
        private fun ChoiceRow(label: String, colors: List<androidx.compose.ui.graphics.Color>, selected: Int, onSelected: (Int) -> Unit) {
            Row(Modifier.fillMaxWidth().padding(top = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(label, color = KalemlikTokens.SecondaryText, fontSize = 12.sp)
                Spacer(Modifier.weight(1f))
                colors.forEachIndexed { index, color ->
                    Box(Modifier.padding(start = 9.dp).size(28.dp).clip(CircleShape).background(color).border(if (selected == index) 2.dp else 0.dp, Color.White, CircleShape).clickable { onSelected(index) })
                }
            }
        }

        @Composable
        private fun FilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
            Text(label, color = if (selected) Color.White else KalemlikTokens.SecondaryText, fontSize = 12.sp, modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(if (selected) KalemlikTokens.Accent else KalemlikTokens.Background).clickable(onClick = onClick).padding(horizontal = 12.dp, vertical = 9.dp))
        }

        private enum class EditorTool(val label: Int) {
            PEN(R.string.pen), HIGHLIGHTER(R.string.highlighter), ERASER(R.string.eraser), SELECTION(R.string.selection), SHAPE(R.string.shape), TEXT(R.string.text), IMAGE(R.string.image), RULER(R.string.ruler)
        }

        @Composable
        private fun EditorScreen(nav: NavHostController, widthClass: WindowWidthSizeClass, vm: NotebookViewModel, name: String, cover: Int) {
            var tool by rememberSaveable { mutableStateOf(EditorTool.PEN.name) }
            var showPenSettings by rememberSaveable { mutableStateOf(true) }
            var selectedColor by rememberSaveable { mutableIntStateOf(0) }
            val tablet = widthClass != WindowWidthSizeClass.Compact
            val activeTool = EditorTool.valueOf(tool)
            if (tablet) {
                Column(Modifier.fillMaxSize().background(KalemlikTokens.Background)) {
                    EditorTopBar(nav, name, activeTool, { next -> if (tool == next.name) showPenSettings = !showPenSettings else { tool = next.name; showPenSettings = next == EditorTool.PEN } }, selectedColor, { selectedColor = it })
                    Row(Modifier.fillMaxSize()) {
                        PageStrip()
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            PaperSurface()
                            if (activeTool == EditorTool.PEN && showPenSettings) PenSettingsPanel(Modifier.align(Alignment.TopEnd).padding(18.dp))
                        }
                    }
                }
            } else {
                Column(Modifier.fillMaxSize().background(KalemlikTokens.Background)) {
                    MobileEditorTopBar(nav, name)
                    Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) { PaperSurface() }
                    ColorRow(selectedColor) { selectedColor = it }
                    MobileTools(activeTool) { next -> tool = next.name }
                    Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
                }
            }
        }

        @Composable
        private fun EditorTopBar(nav: NavHostController, name: String, active: EditorTool, onTool: (EditorTool) -> Unit, selectedColor: Int, onColor: (Int) -> Unit) {
            Row(Modifier.fillMaxWidth().height(64.dp).background(KalemlikTokens.Navigation).padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = { nav.popBackStack() }) { Text("‹  ${stringResource(R.string.back_to_notebooks)}", color = Color.White) }
                Text(name.ifBlank { stringResource(R.string.new_notebook) }, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 18.dp))
                IconButton(onClick = {}) { Icon(Icons.Default.Undo, stringResource(R.string.undo)) }
                IconButton(onClick = {}) { Icon(Icons.Default.Redo, stringResource(R.string.redo)) }
                Divider(Modifier.height(28.dp).width(1.dp), color = KalemlikTokens.Divider)
                Row(Modifier.weight(1f).horizontalScroll(rememberScrollState())) {
                    EditorTool.values().forEach { tool -> ToolButton(tool, tool == active) { onTool(tool) } }
                }
                ColorDots(selectedColor, onColor)
                TextButton(onClick = {}) { Icon(Icons.Default.Share, stringResource(R.string.share)); Spacer(Modifier.width(4.dp)); Text(stringResource(R.string.share)) }
            }
        }

        @Composable
        private fun ToolButton(tool: EditorTool, active: Boolean, onClick: () -> Unit) {
            val icon = when (tool) {
                EditorTool.PEN -> Icons.Default.Edit
                EditorTool.HIGHLIGHTER -> Icons.Default.Brush
                EditorTool.ERASER -> Icons.Default.Delete
                EditorTool.SELECTION -> Icons.Default.Layers
                EditorTool.SHAPE -> Icons.Default.GridView
                EditorTool.TEXT -> Icons.Default.TextFields
                EditorTool.IMAGE -> Icons.Default.Image
                EditorTool.RULER -> Icons.Default.Straighten
            }
            Row(Modifier.height(44.dp).clip(RoundedCornerShape(10.dp)).background(if (active) KalemlikTokens.Accent else Color.Transparent).clickable(onClick = onClick).padding(horizontal = 9.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, stringResource(tool.label), tint = if (active) Color.White else Color(0xFFC9CDD4), modifier = Modifier.size(18.dp))
                Text(stringResource(tool.label), color = if (active) Color.White else Color(0xFFC9CDD4), fontSize = 12.sp, modifier = Modifier.padding(start = 5.dp))
            }
        }

        @Composable
        private fun ColorDots(selected: Int, onSelected: (Int) -> Unit) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(horizontal = 10.dp)) {
                KalemlikTokens.InkPalette.forEachIndexed { index, color ->
                    Box(Modifier.size(24.dp).border(if (index == selected) 2.dp else 0.dp, Color.White, CircleShape).padding(3.dp).clip(CircleShape).background(color).clickable { onSelected(index) })
                }
            }
        }

        @Composable
        private fun PageStrip() {
            Column(Modifier.width(130.dp).fillMaxHeight().background(KalemlikTokens.Navigation).padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.width(88.dp).height(116.dp).background(KalemlikTokens.Paper).border(3.dp, KalemlikTokens.Accent, RoundedCornerShape(4.dp)))
                Spacer(Modifier.weight(1f))
                Text(stringResource(R.string.page_indicator, 1, 1), color = KalemlikTokens.SecondaryText, fontSize = 12.sp)
                TextButton(onClick = {}) { Icon(Icons.Default.Add, stringResource(R.string.add_page)); Text(stringResource(R.string.add_page)) }
            }
        }

        @Composable
        private fun PaperSurface() {
            BoxWithConstraints(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                val paperWidth = maxWidth * 0.72f
                val paperHeight = paperWidth * 1.414f
                Box(Modifier.width(paperWidth).height(paperHeight.coerceAtMost(maxHeight - 24.dp)).shadow(5.dp, RoundedCornerShape(4.dp)).clip(RoundedCornerShape(4.dp)).background(KalemlikTokens.Paper)) {
                    InkCanvas(Modifier.fillMaxSize())
                }
            }
        }

        @Composable
        private fun InkCanvas(modifier: Modifier) {
            Canvas(modifier.pointerInput(Unit) { detectTapGestures { } }) { }
        }

        @Composable
        private fun PenSettingsPanel(modifier: Modifier) {
            var thickness by rememberSaveable { mutableStateOf(0.35f) }
            var shapeDetection by rememberSaveable { mutableStateOf(true) }
            var palmRejection by rememberSaveable { mutableStateOf(true) }
            Column(modifier.width(230.dp).clip(RoundedCornerShape(16.dp)).background(KalemlikTokens.Surface).padding(16.dp)) {
                Text(stringResource(R.string.pen_settings), fontWeight = FontWeight.Bold)
                Text(stringResource(R.string.thickness), color = KalemlikTokens.SecondaryText, fontSize = 12.sp, modifier = Modifier.padding(top = 14.dp))
                Slider(value = thickness, onValueChange = { thickness = it })
                Text(stringResource(R.string.pen_type), color = KalemlikTokens.SecondaryText, fontSize = 12.sp)
                Row(Modifier.horizontalScroll(rememberScrollState())) { listOf(R.string.ballpoint, R.string.fountain, R.string.brush).forEach { FilterChip(stringResource(it), it == R.string.ballpoint) {} } }
                SettingSwitch(stringResource(R.string.shape_detection), shapeDetection) { shapeDetection = it }
                SettingSwitch(stringResource(R.string.palm_rejection), palmRejection) { palmRejection = it }
            }
        }

        @Composable
        private fun SettingSwitch(label: String, checked: Boolean, onChecked: (Boolean) -> Unit) {
            Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(label, fontSize = 12.sp, modifier = Modifier.weight(1f))
                Switch(checked = checked, onCheckedChange = onChecked)
            }
        }

        @Composable
        private fun MobileEditorTopBar(nav: NavHostController, name: String) {
            Row(Modifier.fillMaxWidth().height(92.dp).background(KalemlikTokens.Navigation).padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { nav.popBackStack() }) { Icon(Icons.Default.ArrowBack, stringResource(R.string.back_to_notebooks)) }
                Text(name.ifBlank { stringResource(R.string.new_notebook) }, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                IconButton(onClick = {}) { Icon(Icons.Default.Undo, stringResource(R.string.undo)) }
                IconButton(onClick = {}) { Icon(Icons.Default.MoreHoriz, stringResource(R.string.more)) }
            }
        }

        @Composable
        private fun ColorRow(selected: Int, onSelected: (Int) -> Unit) {
            Row(Modifier.fillMaxWidth().padding(vertical = 10.dp), horizontalArrangement = Arrangement.Center) { ColorDots(selected, onSelected) }
        }

        @Composable
        private fun MobileTools(active: EditorTool, onTool: (EditorTool) -> Unit) {
            Row(Modifier.fillMaxWidth().background(KalemlikTokens.Navigation).horizontalScroll(rememberScrollState()).padding(8.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                listOf(EditorTool.PEN, EditorTool.HIGHLIGHTER, EditorTool.ERASER, EditorTool.SELECTION).forEach { ToolButton(it, active == it) { onTool(it) } }
                ToolButton(EditorTool.SHAPE, false) {}
            }
        }

        @Composable
        private fun SettingsScreen(nav: NavHostController, widthClass: WindowWidthSizeClass) {
            val tablet = widthClass != WindowWidthSizeClass.Compact
            Row(Modifier.fillMaxSize().background(KalemlikTokens.Background)) {
                if (tablet) LibrarySidebar("settings", {}, nav)
                Column(Modifier.fillMaxSize().padding(horizontal = if (tablet) 48.dp else 20.dp, vertical = 24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (!tablet) IconButton(onClick = { nav.popBackStack() }) { Icon(Icons.Default.ArrowBack, stringResource(R.string.back_to_notebooks)) }
                        Text(stringResource(R.string.settings), fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    }
                    LazyColumn(Modifier.fillMaxWidth().padding(top = 22.dp), verticalArrangement = Arrangement.spacedBy(1.dp)) {
                        item { SettingsRow(R.string.theme, R.string.dark, Icons.Default.Visibility) {} }
                        item { SettingsRow(R.string.default_cover, R.string.cover_color, Icons.Default.Palette) {} }
                        item { SettingsRow(R.string.default_template, R.string.blank, Icons.Default.GridView) {} }
                        item { SettingsRow(R.string.pen_settings, R.string.pen_settings, Icons.Default.Edit) {} }
                        item { SettingsRow(R.string.backup, R.string.disabled, Icons.Default.Layers) {} }
                        item { SettingsRow(R.string.about, R.string.app_name, Icons.Default.Info) {} }
                    }
                }
            }
        }

        @Composable
        private fun SettingsRow(title: Int, value: Int, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
            Row(Modifier.fillMaxWidth().height(64.dp).clickable(onClick = onClick).padding(horizontal = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, stringResource(title), tint = KalemlikTokens.SecondaryText, modifier = Modifier.size(22.dp))
                Column(Modifier.weight(1f).padding(start = 16.dp)) {
                    Text(stringResource(title), fontSize = 15.sp)
                    Text(stringResource(value), color = KalemlikTokens.SecondaryText, fontSize = 12.sp)
                }
            }
        }
    }
}
