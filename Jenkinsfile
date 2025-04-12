pipeline {
    agent any

    stages {
        stage('Git') {
            steps {
                git branch : 'EyaABAAB-4TWIN1-G1',
                url : 'https://github.com/firasbessalah/devopsProjet.git'
            }
        }

        stage('Run Tests') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml' 
                }
                failure {
                    echo "Arrêt du pipeline, tests echoués"
                    error("Tests unitaires échoués.")  
                }
            }
        }
        
         stage('Compile') {
            steps {
                sh 'mvn clean compile'
            }
        }
     stage('MVN Sonarqube') {
            steps {
                sh 'mvn sonar:sonar -Dsonar.login=squ_3aa266e36e53995843672eea3d576a69f9fd4eb1 -Dmaven.test.skip=true'
            }
        }
        
    }
}
