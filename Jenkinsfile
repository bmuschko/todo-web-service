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
        stage('Build & Push Image') {
            steps {
                gradlew('jib')
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