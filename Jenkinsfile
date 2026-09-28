// QVerse Jenkins pipeline - parameterised for environment, suite and execution target.
pipeline {
    agent any
    tools { jdk 'jdk21'; maven 'maven3' }

    parameters {
        choice(name: 'ENV', choices: ['qa', 'staging', 'prod'], description: 'Target environment')
        choice(name: 'SUITE', choices: ['smoke', 'regression', 'web', 'api', 'mobile'], description: 'Suite profile')
        choice(name: 'TARGET', choices: ['headless', 'grid', 'cloud'], description: 'Execution target')
        choice(name: 'BROWSER', choices: ['CHROME', 'FIREFOX', 'EDGE'], description: 'Browser')
    }

    options { timestamps(); timeout(time: 90, unit: 'MINUTES') }

    stages {
        stage('Test') {
            steps {
                bat "mvn -B clean test -P${params.ENV},${params.SUITE},${params.TARGET},ci -Dbrowser=${params.BROWSER}"
            }
        }
    }

    post {
        always {
            allure includeProperties: false, results: [[path: 'target/allure-results']]
            archiveArtifacts artifacts: 'target/qverse-dashboard/**, target/logs/**', allowEmptyArchive: true
            junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: true
        }
    }
}
