pipeline {
    agent any

    // triggers {
    //     cron('* * * * *')
    // }

    stages {
        stage('Checkout') {
            steps {
                // Get some code from a GitHub repository
                git branch: 'main', url: 'https://github.com/Jaden-Reklaw/spring-petclinic.git'
            }
        }
        stage('Build') {
            steps {
                sh './mvnw clean package'
            }

            post {
                // If Maven was able to run the tests, even if some of the test
                // failed, record the test results and archive the jar file.
                always {
                    junit '**/target/surefire-reports/TEST-*.xml'
                    archiveArtifacts 'target/*.jar'
                // }
                // changed {
                    emailext subject: 'Job \'${JOB_NAME}\' (${BUILD_NUMBER}) is waiting for input',
                    body: 'Please go to ${BUILD_URL} and verify the build',
                    attachLog: true, 
                    compressLog: true, 
                    to: 'test@jenkins',
                    recipientProviders: [upstreamDevelopers(), requestor()] 
                }
            }
        }
    }
}
