pipeline {
    agent any

    triggers {
        pollSCM('*/5 * * * *')
    }

    stages {
        stage('Compile & Unit Tests') {
            steps {
                gradlew('clean', 'test')
            }
        }
        stage('Integration Tests') {
            steps {
                gradlew('integrationTest')
            }
        }
        stage('Assemble') {
            steps {
                gradlew('assemble')
                stash includes: '**/build/libs/*.jar', name: 'app'
            }
        }
        stage('Build & Push Image') {
            environment {
                DOCKER_USERNAME = ${env.DOCKER_USERNAME}
                DOCKER_PASSWORD = credentials('DOCKER_PASSWORD')
                DOCKER_EMAIL = ${env.DOCKER_EMAIL}
            }
            steps {
                gradlew('dockerPushImage')
            }
        }
    }
    post {
        failure {
            mail to: 'benjamin.muschko@gmail.com', subject: 'Build failed', body: 'Please fix!'
        }
    }
}

def gradlew(String... args) {
    sh "./gradlew ${args.join(' ')} -s"
}