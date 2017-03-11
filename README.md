
```Gradle

def annoationDir="$buildDir/generated/source/annotation"

dependencies {
    ajson fileTree(dir: 'build-src/libs', include: '*.jar')
}

applicationVariants.all { variant ->
    variant.javaCompile.doFirst {

        def aptOutput = file("$annoationDir/$variant.dirName")
        aptOutput.mkdirs()

        variant.javaCompile.options.compilerArgs += [
            '-processorpath',
            configurations.ajson.getAsPath(),
            '-processor',
            'com.dv_soft.greendao.processor.GreenDaoProcessor,com.dv_soft.json.processors.JsonProcessor',
            '-s',
            aptOutput
        ]
    }
}
    
```