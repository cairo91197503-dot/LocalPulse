package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.models.Academy
import com.example.data.models.Business
import com.example.data.models.Enrollment
import com.example.data.models.Review
import com.example.ui.viewmodel.AcademyViewModel
import com.example.ui.viewmodel.BusinessViewModel
import kotlinx.coroutines.launch

// ==========================================
// 1. DASHBOARD TAB
// ==========================================

@Composable
fun DashboardTab(
    bizViewModel: BusinessViewModel,
    academyViewModel: AcademyViewModel,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val businesses by bizViewModel.businessesList.collectAsState()
    val academies by academyViewModel.academiesList.collectAsState()
    val enrollments by academyViewModel.enrollmentsList.collectAsState()

    var generalAiQuery by remember { mutableStateFlowOf("") }
    var generalAiResponse by remember { mutableStateFlowOf("") }
    var isQueryingGeneralAi by remember { mutableStateFlowOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcoming Hero Header Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Welcome to Local Pulse",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Your connected neighborhood is alive. Discover local academies, support community businesses, and build raw skills with real-time AI mentoring.",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp
                )
            }
        }

        // Pulse metrics
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatsCard(
                title = "Local Spots",
                value = businesses.size.toString(),
                icon = Icons.Default.Storefront,
                modifier = Modifier.weight(1f)
            )
            StatsCard(
                title = "Academies",
                value = academies.size.toString(),
                icon = Icons.Default.School,
                modifier = Modifier.weight(1f)
            )
            StatsCard(
                title = "My Courses",
                value = enrollments.size.toString(),
                icon = Icons.Default.BookmarkAdded,
                modifier = Modifier.weight(1f)
            )
        }

        // Neighborhood AI Assistant chatbot
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.BubbleChart,
                        contentDescription = "AI Assistant Logo",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "Pulse AI Guide",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Text(
                    text = "Ask anything about local spots or study courses currently in the catalog! Your AI companion matches and grounds answers in our direct community directory.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )

                OutlinedTextField(
                    value = generalAiQuery,
                    onValueChange = { generalAiQuery = it },
                    placeholder = { Text("e.g. Where can I find coffee or learn coding?") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Button(
                    onClick = {
                        if (generalAiQuery.isNotBlank()) {
                            scope.launch {
                                isQueryingGeneralAi = true
                                generalAiResponse = "Gleaning neighborhood matches..."
                                try {
                                    val prompt = """
                                        The user wants to find some resource or information locally: "$generalAiQuery".
                                        
                                        Here are resources loaded in our community directory:
                                        Businesses: ${businesses.joinToString { "${it.name} (${it.category})" }}
                                        Academies: ${academies.joinToString { it.name }}
                                        
                                        Please provide a warm assistant response advising them of matches or helping them out. Keep the guidance under 3 conversational paragraphs.
                                    """.trimIndent()
                                    
                                    val resp = com.example.data.api.GeminiClient.generateResponse(
                                        prompt = prompt,
                                        systemInstruction = "You are 'Pulse AI Guide', a professional local community assistant helping residents."
                                    )
                                    generalAiResponse = resp
                                } catch (e: Exception) {
                                    generalAiResponse = "AI Call failed: ${e.message}"
                                } finally {
                                    isQueryingGeneralAi = false
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isQueryingGeneralAi) {
                        CircularProgressIndicator(size = 18.dp, color = Color.White)
                    } else {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Send")
                            Text("Consult Neighborhood AI")
                        }
                    }
                }

                if (generalAiResponse.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "LIVELY RECOMMENDATION",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = generalAiResponse,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatsCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ==========================================
// 2. DIRECTORY TAB (BUSINESSES)
// ==========================================

@Composable
fun DirectoryTab(viewModel: BusinessViewModel) {
    val businesses by viewModel.businessesList.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedBusiness by viewModel.selectedBusiness.collectAsState()
    val aiRecommendations by viewModel.aiRecommendations.collectAsState()
    val isLoadingAi by viewModel.isLoadingAi.collectAsState()
    
    var searchQuery by remember { mutableStateFlowOf("") }
    var aiQueryInput by remember { mutableStateFlowOf("") }
    var reviewTextInput by remember { mutableStateFlowOf("") }
    var reviewRatingInput by remember { mutableStateFlowOf(5f) }

    val categories = listOf("All", "Restaurant", "Retail", "Services", "Health", "Leisure")

    val filteredList = businesses.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.description.contains(searchQuery, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Community Business Hub",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search businesses...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category Chips Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Scrollable helper or manual row to hold chips
                Box(modifier = Modifier.fillMaxWidth()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        categories.take(3).forEach { cat ->
                            CategoryFilterChip(
                                label = cat,
                                selected = (cat == "All" && selectedCategory == null) || (cat == selectedCategory),
                                onClick = {
                                    viewModel.selectCategory(if (cat == "All") null else cat)
                                }
                            )
                        }
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.drop(3).forEach { cat ->
                    CategoryFilterChip(
                        label = cat,
                        selected = (cat == "All" && selectedCategory == null) || (cat == selectedCategory),
                        onClick = {
                            viewModel.selectCategory(if (cat == "All") null else cat)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Smart recommendation block
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Spark Spot Finder (Gemini AI)",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "Need specialized suggestions? Tell AI your mood or preference, and match current local directory spots instantly.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                    )
                    OutlinedTextField(
                        value = aiQueryInput,
                        onValueChange = { aiQueryInput = it },
                        placeholder = { Text("e.g. Cozy place to read books or get a haircut") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Button(
                        onClick = {
                            if (aiQueryInput.isNotBlank()) {
                                viewModel.getAiBusinessSuggestions(aiQueryInput)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isLoadingAi) {
                            CircularProgressIndicator(size = 18.dp, color = Color.White)
                        } else {
                            Text("Recommend Spots")
                        }
                    }

                    if (aiRecommendations.isNotEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "AI RECOMMENDATION",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    IconButton(
                                        onClick = { viewModel.clearAiRecommendations() },
                                        modifier = Modifier.size(16.dp)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(12.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = aiRecommendations,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        if (filteredList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No businesses match your search or filter.", color = Color.Gray)
                }
            }
        } else {
            items(filteredList) { biz ->
                BusinessItemCard(
                    business = biz,
                    onClick = { viewModel.selectBusiness(biz) },
                    onRecommend = { viewModel.recommendBusiness(biz.id) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Detail & review screen dialog
    selectedBusiness?.let { biz ->
        val reviews by viewModel.selectedBusinessReviews.collectAsState()
        Dialog(onDismissRequest = { viewModel.selectBusiness(null) }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = biz.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        IconButton(onClick = { viewModel.selectBusiness(null) }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Text(
                        text = biz.category.uppercase(),
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(text = biz.description, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.Place, contentDescription = "Location", tint = Color.Gray, modifier = Modifier.size(16.dp))
                                    Text(text = biz.address, fontSize = 12.sp, color = Color.Gray)
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.Phone, contentDescription = "Phone", tint = Color.Gray, modifier = Modifier.size(16.dp))
                                    Text(text = biz.contact, fontSize = 12.sp, color = Color.Gray)
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    Text(
                                        text = "${biz.rating} ★",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                    Text(
                                        text = "${biz.recommendedCount} recommendations",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        // Write review
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(text = "Submit Review", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    
                                    // Custom stars
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        (1..5).forEach { star ->
                                            Icon(
                                                imageVector = if (star <= reviewRatingInput) Icons.Default.Star else Icons.Default.StarBorder,
                                                contentDescription = "Star $star",
                                                tint = MaterialTheme.colorScheme.tertiary,
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .clickable { reviewRatingInput = star.toFloat() }
                                            )
                                        }
                                    }

                                    OutlinedTextField(
                                        value = reviewTextInput,
                                        onValueChange = { reviewTextInput = it },
                                        placeholder = { Text("How was your experience?") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp)
                                    )

                                    Button(
                                        onClick = {
                                            if (reviewTextInput.isNotBlank()) {
                                                viewModel.submitReview(biz.id, reviewRatingInput, reviewTextInput)
                                                reviewTextInput = ""
                                            }
                                        },
                                        modifier = Modifier.align(Alignment.End),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Submit")
                                    }
                                }
                            }
                        }

                        // Display Reviews list
                        item {
                            Text(text = "Visitor Reviews (${reviews.size})", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }

                        if (reviews.isEmpty()) {
                            item {
                                Text("No reviews left yet. Be the first!", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                        } else {
                            items(reviews) { r ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = r.userEmail, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            Text(text = "${r.rating}★", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = r.reviewText, fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryFilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.height(36.dp)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun BusinessItemCard(
    business: Business,
    onClick: () -> Unit,
    onRecommend: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = business.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = business.category,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = business.description,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Default.Star, contentDescription = "Rating Star", tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(16.dp))
                    Text(text = "${business.rating} ★", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Button(
                    onClick = { onRecommend() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ThumbUp,
                            contentDescription = "Recommend",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "${business.recommendedCount}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// 3. ACADEMIES TAB (COURSES & SKILLS)
// ==========================================

@Composable
fun AcademiesTab(viewModel: AcademyViewModel) {
    val academies by viewModel.academiesList.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedAcademy by viewModel.selectedAcademy.collectAsState()
    val aiRecommendations by viewModel.aiRecommendations.collectAsState()
    val isLoadingAi by viewModel.isLoadingAi.collectAsState()

    var searchQuery by remember { mutableStateFlowOf("") }
    var userGoalInput by remember { mutableStateFlowOf("") }

    val categories = listOf("All", "Technology", "Arts", "Language")

    val filteredList = academies.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.description.contains(searchQuery, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Skill Academies & Education",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search skill academies...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category Chips Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    CategoryFilterChip(
                        label = cat,
                        selected = (cat == "All" && selectedCategory == null) || (cat == selectedCategory),
                        onClick = {
                            viewModel.selectCategory(if (cat == "All") null else cat)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Study suggestions by AI
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Tailored Study Advisor (Gemini AI)",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "What skill, hobby, or career goal do you want to master? Let AI scout our local academies and plan your match.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    OutlinedTextField(
                        value = userGoalInput,
                        onValueChange = { userGoalInput = it },
                        placeholder = { Text("e.g. Build an app, or learn Spanish for travel") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Button(
                        onClick = {
                            if (userGoalInput.isNotBlank()) {
                                viewModel.getAiCourseRecommendations(userGoalInput)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isLoadingAi) {
                            CircularProgressIndicator(size = 18.dp, color = Color.White)
                        } else {
                            Text("Map Learning Opportunities")
                        }
                    }

                    if (aiRecommendations.isNotEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "AI ADVISORY",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    IconButton(
                                        onClick = { viewModel.clearAiRecommendations() },
                                        modifier = Modifier.size(16.dp)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(12.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = aiRecommendations,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        if (filteredList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No academies match your search or filter.", color = Color.Gray)
                }
            }
        } else {
            items(filteredList) { academy ->
                AcademyItemCard(
                    academy = academy,
                    onClick = { viewModel.selectAcademy(academy) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Academy Detail Dialog
    selectedAcademy?.let { academy ->
        val courses by viewModel.selectedAcademyCourses.collectAsState()
        val reviews by viewModel.selectedAcademyReviews.collectAsState()

        var showEnrollDialog by remember { mutableStateOf<Course?>(null) }
        var studyGoalText by remember { mutableStateOf("") }
        var reviewText by remember { mutableStateOf("") }
        var reviewRating by remember { mutableStateOf(5f) }

        Dialog(onDismissRequest = { viewModel.selectAcademy(null) }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = academy.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        IconButton(onClick = { viewModel.selectAcademy(null) }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Text(
                        text = academy.category.uppercase(),
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(text = academy.description, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.Place, contentDescription = "Location", tint = Color.Gray, modifier = Modifier.size(16.dp))
                                    Text(text = academy.address, fontSize = 12.sp, color = Color.Gray)
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.Phone, contentDescription = "Contact", tint = Color.Gray, modifier = Modifier.size(16.dp))
                                    Text(text = academy.contact, fontSize = 12.sp, color = Color.Gray)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(text = "Award: ${academy.certificateInfo}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Courses Header
                        item {
                            Text("Available Curriculum (${courses.size} courses)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }

                        if (courses.isEmpty()) {
                            item {
                                Text("No courses listed yet.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                        } else {
                            items(courses) { course ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = course.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(1f))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(MaterialTheme.colorScheme.secondaryContainer)
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(text = course.level, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSecondaryContainer, fontWeight = FontWeight.SemiBold)
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = "Duration: ${course.duration}", fontSize = 11.sp, color = Color.Gray)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(text = course.syllabusOverview, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(modifier = Modifier.height(12.dp))

                                        if (course.userEnrolled) {
                                            Button(
                                                onClick = { /* Detail under MyDesk */ },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(8.dp),
                                                enabled = false
                                            ) {
                                                Text("Enrolled (Check Desk Tab)")
                                            }
                                        } else {
                                            Button(
                                                onClick = { showEnrollDialog = course },
                                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(Icons.Default.School, contentDescription = "Enroll", modifier = Modifier.size(16.dp))
                                                    Text("Enroll with AI Tutor Path")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Write review section
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(text = "Submit Review for Academy", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        (1..5).forEach { star ->
                                            Icon(
                                                imageVector = if (star <= reviewRating) Icons.Default.Star else Icons.Default.StarBorder,
                                                contentDescription = "Star $star",
                                                tint = MaterialTheme.colorScheme.tertiary,
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clickable { reviewRating = star.toFloat() }
                                            )
                                        }
                                    }
                                    OutlinedTextField(
                                        value = reviewText,
                                        onValueChange = { reviewText = it },
                                        placeholder = { Text("How was your learning experience?") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    Button(
                                        onClick = {
                                            if (reviewText.isNotBlank()) {
                                                viewModel.submitReview(academy.id, reviewRating, reviewText)
                                                reviewText = ""
                                            }
                                        },
                                        modifier = Modifier.align(Alignment.End),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Submit")
                                    }
                                }
                            }
                        }

                        item {
                            Text("What Students Say (${reviews.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        if (reviews.isEmpty()) {
                            item {
                                Text("No reviews yet. Be the first!", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                        } else {
                            items(reviews) { r ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = r.userEmail, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            Text(text = "${r.rating}★", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = r.reviewText, fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Inside enroll dialog: Personal Goal capture
        showEnrollDialog?.let { course ->
            Dialog(onDismissRequest = { showEnrollDialog = null }) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Personalize Your Learning",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "You are enrolling in: ${course.title}.\nDescribe what you want to build or achieve (e.g. 'I want to build a local commerce mobile app' or 'I want to speak Spanish at local meetings'). Gemini will automatically draft a custom study guide mapping this course syllabus to your specific ambition!",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedTextField(
                            value = studyGoalText,
                            onValueChange = { studyGoalText = it },
                            placeholder = { Text("My personal goal / target project...") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 4,
                            shape = RoundedCornerShape(12.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showEnrollDialog = null },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancel")
                            }
                            Button(
                                onClick = {
                                    viewModel.enrollInCourse(course.id, academy.id, studyGoalText)
                                    showEnrollDialog = null
                                    studyGoalText = ""
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Confirm Enrollment")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AcademyItemCard(academy: Academy, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = academy.name,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = academy.category,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = academy.description,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${academy.coursesCount} syllabus courses",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = "View Classes →",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// ==========================================
// 4. MY LEARNING DESK TAB
// ==========================================

@Composable
fun MyDeskTab(viewModel: AcademyViewModel) {
    val enrollments by viewModel.enrollmentsList.collectAsState()
    var selectedEnrollment by remember { mutableStateOf<Enrollment?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "My Personal Learning Desk",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Track your course enrollment metrics, review AI generated target schedules, and mark milestones.",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        if (enrollments.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "Empty",
                            tint = Color.LightGray,
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = "You are not enrolled in any courses yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                        Text(
                            text = "Head to the Academies tab to enroll. You will receive an AI Tutor schedule tailored to your exact project ambition!",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        } else {
            items(enrollments) { enrollment ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedEnrollment = enrollment },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = enrollment.courseTitle,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = enrollment.status,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Personal Study Plan Available! Tap to unfold week-by-week study targets customized by Gemini AI.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Tutor Strategy: Available",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "View Study Plan →",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }

    // Detail study schedule view
    selectedEnrollment?.let { enr ->
        Dialog(onDismissRequest = { selectedEnrollment = null }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = enr.courseTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { selectedEnrollment = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "AI GENERATED PERSONALIZED STUDY STRATEGY",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = enr.studyPathSuggestion,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.leaveCourse(enr)
                            selectedEnrollment = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Withdraw", tint = Color.White, modifier = Modifier.size(16.dp))
                            Text("Withdraw from Course", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
