package com.example.dietlens.feature.home

import androidx.annotation.StringRes
import com.example.dietlens.R

enum class ProductCategoryEnum(
    @StringRes val titleRes: Int,
    val apiTag: String
) {
    ALL(R.string.cat_all, "all"),
    BEVERAGES(R.string.cat_beverages, "en:beverages"),
    JUICES(R.string.cat_juices, "en:juices"),
    CARBONATED_DRINKS(R.string.cat_carbonated_drinks, "en:carbonated-drinks"),
    TEAS_AND_COFFEES(R.string.cat_teas_and_coffees, "en:teas-and-coffees"),
    ALCOHOLIC_BEVERAGES(R.string.cat_alcoholic_beverages, "en:alcoholic-beverages"),
    SNACKS(R.string.cat_snacks, "en:snacks"),
    SWEET_SNACKS(R.string.cat_sweet_snacks, "en:sweet-snacks"),
    SALTY_SNACKS(R.string.cat_salty_snacks, "en:salty-snacks"),
    CHOCOLATES(R.string.cat_chocolates, "en:chocolates"),
    CRISPS(R.string.cat_crisps, "en:crisps"),
    BISCUITS_AND_CAKES(R.string.cat_biscuits_and_cakes, "en:biscuits-and-cakes"),
    DESSERTS(R.string.cat_desserts, "en:desserts"),
    DAIRIES(R.string.cat_dairies, "en:dairies"),
    MILKS(R.string.cat_milks, "en:milks"),
    YOGURTS(R.string.cat_yogurts, "en:yogurts"),
    CHEESES(R.string.cat_cheeses, "en:cheeses"),
    PLANT_BASED_FOODS_AND_BEVERAGES(R.string.cat_plant_based_foods_and_beverages, "en:plant-based-foods-and-beverages"),
    PLANT_BASED_MILKS(R.string.cat_plant_based_milks, "en:plant-based-milks"),
    MEALS(R.string.cat_meals, "en:meals"),
    SAUCES(R.string.cat_sauces, "en:sauces"),
    GROCERIES(R.string.cat_groceries, "en:groceries"),
    PASTAS(R.string.cat_pastas, "en:pastas"),
    CANNED_FOODS(R.string.cat_canned_foods, "en:canned-foods"),
    BREAKFASTS(R.string.cat_breakfasts, "en:breakfasts"),
    BREADS(R.string.cat_breads, "en:breads"),
    SOUPS(R.string.cat_soups, "en:soups"),
    MEATS(R.string.cat_meats, "en:meats"),
    SEAFOOD(R.string.cat_seafood, "en:seafood"),
    PLANT_BASED_FOODS(R.string.cat_plant_based_foods, "en:plant-based-foods"),
    FROZEN_FOODS(R.string.cat_frozen_foods, "en:frozen-foods");

    companion object {

        fun getAllDisplayNames(context: android.content.Context): List<String> =
            values().map { context.getString(it.titleRes) }


        fun fromDisplayName(context: android.content.Context, displayName: String): ProductCategoryEnum? =
            values().firstOrNull { context.getString(it.titleRes) == displayName }
    }
}
