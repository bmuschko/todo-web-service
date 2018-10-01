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
            post {
                always {
                    junit '**/build/test-results/test/TEST-*.xml'
                }
            }
        }
        stage('Integration Tests') {
            steps {
                gradlew('integrationTest')
            }
            post {
                always {
                    junit '**/build/test-results/integrationTest/TEST-*.xml'
                }
            }
        }
        stage('Assembly') {
            steps {
                gradlew('assemble')
                stash includes: '**/build/libs/*.jar', name: 'app'
            }
        }
        stage('Build Image') {
            steps {
                gradlew('dockerBuildImage')
            }
        }
        stage('Functional Tests') {
            steps {
                gradlew('functionalTest')
            }
            post {
                always {
                    junit '**/build/test-results/functionalTest/TEST-*.xml'
                }
            }
        }
        stage('Push Image') {
            environment {
                DOCKER_USERNAME = "${env.DOCKER_USERNAME}"
                DOCKER_PASSWORD = credentials('DOCKER_PASSWORD')
                DOCKER_EMAIL = "${env.DOCKER_EMAIL}"
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