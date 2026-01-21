# PixaCompose Global Toast & Snackbar - Quick Start Guide

## 🚀 Quick Setup (3 Steps)

### Step 1: Add Global Hosts to App Root
```kotlin
@Composable
fun App() {
    AppTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            // Add these once at app root
            GlobalToastHost(position = ToastPosition.Bottom)
            GlobalSnackbarHost()
            
            // Your app content
            Scaffold {
                Navigation()
            }
        }
    }
}
```

### Step 2: Use from ViewModel
```kotlin
class MyViewModel : ViewModel() {
    fun onSaveClick() {
        viewModelScope.launch {
            try {
                repository.save()
                PixaToastManager.showSuccess("Saved!")
                PixaSnackbarManager.showSuccess(
                    message = "Changes saved",
                    actionLabel = "Undo",
                    onAction = { undo() }
                )
            } catch (e: Exception) {
                PixaToastManager.showError("Failed")
                PixaSnackbarManager.showErrorFromException(e)
            }
        }
    }
}
```

### Step 3: Use from Composables
```kotlin
@Composable
fun MyScreen() {
    val toastScope = rememberToastScope()
    val snackbarScope = rememberSnackbarScope()
    
    Button(onClick = {
        toastScope.showSuccess("Quick notification")
        snackbarScope.showSuccess(
            message = "Item deleted",
            actionLabel = "Undo"
        )
    }) {
        Text("Delete")
    }
}
```

---

## 📋 API Reference

### Toast Manager Methods

```kotlin
// From ViewModel/UseCase/Repository (suspend context)
suspend fun PixaToastManager.showToast(...)
suspend fun PixaToastManager.showSuccess(message: String)
suspend fun PixaToastManager.showError(message: String)
suspend fun PixaToastManager.showWarning(message: String)
suspend fun PixaToastManager.showInfo(message: String)
suspend fun PixaToastManager.showErrorFromException(exception: Throwable)
suspend fun PixaToastManager.dismissToast(id: Long)
suspend fun PixaToastManager.dismissAll()

// From non-suspend context
fun PixaToastManager.launch { /* suspend block */ }
fun launchToast { /* suspend block */ }

// From Composable
@Composable fun rememberToastScope(): ToastScope
toastScope.showSuccess(message)
toastScope.showError(message)
toastScope.dismissAll()
```

### Snackbar Manager Methods

```kotlin
// From ViewModel/UseCase/Repository (suspend context)
suspend fun PixaSnackbarManager.showSnackbar(...)
suspend fun PixaSnackbarManager.showSuccess(message: String, actionLabel: String? = null, onAction: (() -> Unit)? = null)
suspend fun PixaSnackbarManager.showError(message: String, actionLabel: String? = null, onAction: (() -> Unit)? = null)
suspend fun PixaSnackbarManager.showWarning(message: String, actionLabel: String? = null, onAction: (() -> Unit)? = null)
suspend fun PixaSnackbarManager.showInfo(message: String, actionLabel: String? = null, onAction: (() -> Unit)? = null)
suspend fun PixaSnackbarManager.showErrorFromException(exception: Throwable)
suspend fun PixaSnackbarManager.dismissCurrent()

// From non-suspend context
fun PixaSnackbarManager.launch { /* suspend block */ }
fun launchSnackbar { /* suspend block */ }

// From Composable
@Composable fun rememberSnackbarScope(): SnackbarScope
snackbarScope.showSuccess(message, actionLabel, onAction)
snackbarScope.showError(message, actionLabel, onAction)
snackbarScope.dismissCurrent()
```

---

## 💡 Common Use Cases

### 1. Simple Success Toast
```kotlin
viewModelScope.launch {
    PixaToastManager.showSuccess("Item saved!")
}
```

### 2. Error with Action Snackbar
```kotlin
viewModelScope.launch {
    PixaSnackbarManager.showError(
        message = "Failed to upload",
        actionLabel = "Retry",
        onAction = { retryUpload() }
    )
}
```

### 3. Delete with Undo
```kotlin
viewModelScope.launch {
    val deletedItem = repository.delete(itemId)
    PixaSnackbarManager.showSuccess(
        message = "Item deleted",
        actionLabel = "Undo",
        onAction = { repository.restore(deletedItem) }
    )
}
```

### 4. Exception Handling
```kotlin
viewModelScope.launch {
    try {
        performAction()
    } catch (e: Exception) {
        PixaToastManager.showErrorFromException(e)
        // or with more control
        PixaSnackbarManager.showErrorFromException(
            exception = e,
            message = "Operation failed",
            actionLabel = "Retry",
            onAction = { retryAction() }
        )
    }
}
```

### 5. From Repository/UseCase
```kotlin
class SyncDataUseCase {
    suspend operator fun invoke() {
        try {
            syncData()
            PixaToastManager.showSuccess("Data synced")
        } catch (e: NetworkException) {
            PixaSnackbarManager.showError(
                message = "Network error",
                actionLabel = "Retry",
                onAction = { invoke() }
            )
        }
    }
}
```

### 6. From Composable (Button Click)
```kotlin
@Composable
fun MyScreen() {
    val snackbarScope = rememberSnackbarScope()
    
    Button(onClick = {
        snackbarScope.showSuccess(
            message = "Settings updated",
            actionLabel = "View",
            onAction = { navigateToSettings() }
        )
    }) {
        Text("Save Settings")
    }
}
```

### 7. Non-Suspend Context
```kotlin
fun onDataReceived() {
    launchToast {
        showInfo("New data available")
    }
    
    launchSnackbar {
        showSuccess(
            message = "Data received",
            actionLabel = "View"
        )
    }
}
```

### 8. Custom Duration
```kotlin
viewModelScope.launch {
    PixaSnackbarManager.showSnackbar(
        message = "Processing...",
        duration = SnackbarDuration.Indefinite
    )
    
    // Later...
    PixaSnackbarManager.dismissCurrent()
}
```

---

## 🎨 Customization Options

### Toast Customization
```kotlin
PixaToastManager.showToast(
    message = "Custom toast",
    variant = ToastVariant.Success,
    duration = ToastDuration.Long,
    style = ToastStyle.Outlined,
    icon = customIcon,
    showIcon = true,
    dismissible = true,
    actionText = "Action",
    onAction = { /* action */ },
    customColors = ToastColors(...)
)
```

### Snackbar Customization
```kotlin
PixaSnackbarManager.showSnackbar(
    message = "Custom snackbar",
    actionLabel = "Action",
    variant = SnackbarVariant.Warning,
    duration = SnackbarDuration.Long,
    withDismissAction = true,
    icon = customIcon,
    showIcon = true,
    onAction = { /* action */ },
    onDismiss = { /* dismissed */ },
    customColors = SnackbarColors(...)
)
```

---

## 🧪 Testing

### Mock for Unit Tests
```kotlin
@Composable
fun TestableScreen() {
    val testToast = rememberToastHostState()
    val testSnackbar = rememberSnackbarHostState()
    
    CompositionLocalProvider(
        LocalToastManager provides testToast,
        LocalSnackbarManager provides testSnackbar
    ) {
        // Your screen using global managers
        // Will use test instances instead
    }
}
```

---

## 📦 Variants Available

### Toast Variants
- `ToastVariant.Default` - Neutral gray
- `ToastVariant.Info` - Blue
- `ToastVariant.Success` - Green
- `ToastVariant.Warning` - Orange
- `ToastVariant.Error` - Red

### Snackbar Variants
- `SnackbarVariant.Default` - Dark surface
- `SnackbarVariant.Info` - Blue
- `SnackbarVariant.Success` - Green
- `SnackbarVariant.Warning` - Orange
- `SnackbarVariant.Error` - Red

### Durations
**Toast:**
- `ToastDuration.Short` - 2s
- `ToastDuration.Medium` - 3s
- `ToastDuration.Long` - 5s
- `ToastDuration.Unlimited` - Manual dismiss only

**Snackbar:**
- `SnackbarDuration.Short` - 4s
- `SnackbarDuration.Long` - 10s
- `SnackbarDuration.Indefinite` - Until action/dismiss

---

## ✨ Key Features

✅ **Global Access** - Use from anywhere (ViewModel, UseCase, Repository, Composable)  
✅ **Thread-Safe** - Mutex-based synchronization  
✅ **Type-Safe** - Full Kotlin type safety  
✅ **Backward Compatible** - Existing local usage still works  
✅ **Testable** - CompositionLocal override for tests  
✅ **Action Support** - Buttons with callbacks  
✅ **Auto-Queue** - Multiple toasts/snackbars queue automatically  
✅ **Swipe Dismiss** - Gesture support (Toast)  
✅ **Custom Colors** - Full theming support  
✅ **Icon Support** - Optional icons  

---

## 🔄 Migration from Local State

### Before (Local State)
```kotlin
@Composable
fun MyScreen() {
    val toastState = rememberToastHostState()
    val snackbarState = rememberSnackbarHostState()
    
    Box {
        ToastHost(hostState = toastState)
        SnackbarHost(hostState = snackbarState)
        
        // Pass state to viewmodel or use locally
    }
}
```

### After (Global State)
```kotlin
// In App.kt (once)
GlobalToastHost()
GlobalSnackbarHost()

// In Screen (no state needed)
@Composable
fun MyScreen() {
    // Just use the managers directly
}

// In ViewModel
viewModelScope.launch {
    PixaToastManager.showSuccess("Done!")
    PixaSnackbarManager.showSuccess("Done!")
}
```

---

## 📚 More Information

For complete documentation and advanced usage, see:
- `Toast.kt` - Full implementation with examples
- `Snackbar.kt` - Full implementation with examples
- `PIXA_TOAST_ENHANCEMENTS.md` - Enhancement requirements
- `IMPLEMENTATION_SUMMARY.md` - Implementation details

---

## 💬 Support

For issues or questions:
1. Check the comprehensive usage examples in the source files
2. Review the implementation summary
3. Test with local CompositionLocal override for debugging
