package towerScreen


sealed interface TowerScreenEvent{

    data class OnNavigateTo (val screen : TowerScreenViewModel.NavigationTower): TowerScreenEvent
}