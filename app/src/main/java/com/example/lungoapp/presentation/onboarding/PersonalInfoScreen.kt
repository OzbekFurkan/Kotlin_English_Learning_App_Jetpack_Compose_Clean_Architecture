package com.example.lungoapp.presentation.onboarding

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private const val TAG = "PersonalInfoScreen"

@Composable
fun PersonalInfoScreen(
    viewModel: OnboardingViewModel,
    onNextClick: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("") }
    var selectedEduStatus by remember { mutableStateOf("") }
    var selectedPrevEduYear by remember { mutableStateOf("") }

    // Collect the current personal info state
    val currentPersonalInfo by viewModel.personalInfo.collectAsState()
    
    // Log when personal info changes
    LaunchedEffect(currentPersonalInfo) {
        Log.d(TAG, "Personal info updated in screen: $currentPersonalInfo")
    }

    val genderOptions = listOf("Male", "Female")
    val eduStatusOptions = listOf(
        "First School",
        "Middle School",
        "High School",
        "Associate Degree",
        "Bachelor's Degree",
        "Master's Degree",
        "Higher Education"
    )
    val prevEduYearOptions = listOf(
        "0-1 year",
        "1-3 years",
        "3-5 years",
        "5+ years"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Tell us about yourself",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = age,
            onValueChange = { age = it },
            label = { Text("Age") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        var genderExpanded by remember { mutableStateOf(false) }
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = selectedGender,
                onValueChange = { },
                readOnly = true,
                label = { Text("Gender") },
                trailingIcon = { 
                    IconButton(onClick = { genderExpanded = !genderExpanded }) {
                        Icon(
                            imageVector = if (genderExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.ArrowDropDown,
                            contentDescription = null
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            DropdownMenu(
                expanded = genderExpanded,
                onDismissRequest = { genderExpanded = false }
            ) {
                genderOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = { 
                            selectedGender = option
                            genderExpanded = false
                        }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        var eduStatusExpanded by remember { mutableStateOf(false) }
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = selectedEduStatus,
                onValueChange = { },
                readOnly = true,
                label = { Text("Current Education Status") },
                trailingIcon = { 
                    IconButton(onClick = { eduStatusExpanded = !eduStatusExpanded }) {
                        Icon(
                            imageVector = if (eduStatusExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.ArrowDropDown,
                            contentDescription = null
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            DropdownMenu(
                expanded = eduStatusExpanded,
                onDismissRequest = { eduStatusExpanded = false }
            ) {
                eduStatusOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = { 
                            selectedEduStatus = option
                            eduStatusExpanded = false
                        }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        var prevEduYearExpanded by remember { mutableStateOf(false) }
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = selectedPrevEduYear,
                onValueChange = { },
                readOnly = true,
                label = { Text("Previous English Education") },
                trailingIcon = { 
                    IconButton(onClick = { prevEduYearExpanded = !prevEduYearExpanded }) {
                        Icon(
                            imageVector = if (prevEduYearExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.ArrowDropDown,
                            contentDescription = null
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            DropdownMenu(
                expanded = prevEduYearExpanded,
                onDismissRequest = { prevEduYearExpanded = false }
            ) {
                prevEduYearOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = { 
                            selectedPrevEduYear = option
                            prevEduYearExpanded = false
                        }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = {
                // Map the selected values to the required format
                val gender = when (selectedGender) {
                    "Male" -> 1
                    "Female" -> 2
                    else -> 1 // Default to male
                }

                val educationStatus = when (selectedEduStatus) {
                    "First School" -> 1
                    "Middle School" -> 2
                    "High School" -> 3
                    "Associate Degree" -> 4
                    "Bachelor's Degree" -> 5
                    "Master's Degree" -> 6
                    "Higher Education" -> 7
                    else -> 2 // Default to middle school
                }

                val educationYears = when (selectedPrevEduYear) {
                    "0-1 year" -> 1
                    "1-3 years" -> 2
                    "3-5 years" -> 4
                    "5+ years" -> 5
                    else -> 1 // Default to 0-1 year
                }

                Log.d(TAG, "Updating personal info with:")
                Log.d(TAG, "Name: $name")
                Log.d(TAG, "Age: $age")
                Log.d(TAG, "Gender: $gender")
                Log.d(TAG, "Education Status: $educationStatus")
                Log.d(TAG, "Education Years: $educationYears")

                viewModel.updatePersonalInfo(
                    name = name,
                    age = age,
                    occupation = "", // Not collected in the form
                    gender = gender,
                    educationStatus = educationStatus,
                    educationYears = educationYears
                )

                // Wait a moment to ensure the state is updated
                Log.d(TAG, "Current personal info after update: ${viewModel.personalInfo.value}")
                
                onNextClick()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = name.isNotBlank() && 
                     age.isNotBlank() &&
                     selectedGender.isNotBlank() && 
                     selectedEduStatus.isNotBlank() && 
                     selectedPrevEduYear.isNotBlank()
        ) {
            Text("Next")
        }
    }
} 