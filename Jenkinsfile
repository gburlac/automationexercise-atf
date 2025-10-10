pipeline {
    agent any

    environment {
        MAVEN_OPTS = '-Dmaven.repo.local=.m2/repository'
        ALLURE_RESULTS = 'target/allure-results'
        ALLURE_REPORT = 'target/allure-report'
    }

    tools {
        maven 'Maven 3.9.6' // or your Jenkins Maven tool name
        jdk 'jdk-17'         // or your Jenkins JDK tool name
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Install Browsers') {
            steps {
                sh 'mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install"'
            }
        }
        stage('Build & Test') {
            steps {
                sh 'mvn clean test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                    cucumber 'target/cucumber.json'
                }
            }
        }
        stage('Allure Report') {
            steps {
                sh 'mvn allure:report'
                allure includeProperties: false, jdk: '', results: [[path: env.ALLURE_RESULTS]]
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'target/**/*.html, target/**/*.json, target/allure-report/**', allowEmptyArchive: true
        }
        failure {
            mail to: 'team@example.com', subject: "Build Failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}", body: "Check Jenkins for details."
        }
    }
}

