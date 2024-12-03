package com.example.fitbattleandroid.ui.state

data class MapScreenUiState(
    val locationPermissionState: LocationPermissionState = LocationPermissionState(),
    val permissionDialogState: PermissionDialogState = PermissionDialogState(),
)

data class LocationPermissionState(
    val accessFineLocationState: Boolean = false,
    val accessCoarseLocationState: Boolean = false,
    val backgroundPermissionGranted: Boolean = false,
)

data class PermissionDialogState(
    val showRequestLocationPermissionDialog: Boolean = false, // 位置情報権限のリクエストダイアログ
    val showUpgradeToPreciseLocationDialog: Boolean = false, // 正確な位置情報のリクエストダイアログ
    val showRequestBackgroundPermissionDialog: Boolean = false, // バックグラウンドの位置情報権限のリクエストダイアログ
)
