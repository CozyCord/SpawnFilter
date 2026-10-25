import gg.meza.stonecraft.mod

plugins {
    id("gg.meza.stonecraft")
}

modSettings {
}

repositories {
    mavenCentral()
}

dependencies {
    if (mod.isFabric) {
        modImplementation("net.fabricmc.fabric-api:fabric-api:${mod.prop("fabric_version")}")
    }

    include(implementation("com.electronwill.night-config:core:3.8.1")!!)
    include(implementation("com.electronwill.night-config:toml:3.8.1")!!)
}

sourceSets {
    main {
        resources {
            srcDir("src/main/resources")
        }
    }
}
