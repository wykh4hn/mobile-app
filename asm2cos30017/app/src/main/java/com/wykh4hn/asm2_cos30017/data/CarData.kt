package com.wykh4hn.asm2_cos30017.data

import com.wykh4hn.asm2_cos30017.R
import com.wykh4hn.asm2_cos30017.model.Car

/**
 * The five cars the company rents out. They are created in memory when the app
 * starts and are never written to disk, as the assignment requires.
 *
 * Prices are chosen so every credit rule can be shown in the demo:
 * Tesla x 7 days = 490 (over the 400 limit), VinFast x 5 days = exactly 400 (allowed).
 */
object CarData {

    val cars: List<Car> = listOf(
        Car(1, "Toyota", "Corolla", 2021, 4.5f, 32_000, 45, "Sedan · 5 seats", R.drawable.car_corolla),
        Car(2, "Tesla", "Model 3", 2023, 4.8f, 9_800, 70, "Electric · 5 seats", R.drawable.car_tesla),
        Car(3, "Honda", "City", 2020, 4.2f, 48_000, 35, "Sedan · 5 seats", R.drawable.car_city),
        Car(4, "Mazda", "CX-5", 2022, 4.6f, 21_000, 60, "SUV · 5 seats", R.drawable.car_cx5),
        Car(5, "VinFast", "VF 8", 2024, 4.4f, 5_200, 80, "Electric SUV · 5 seats", R.drawable.car_vf8)
    )
}