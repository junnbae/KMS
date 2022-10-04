pipeline {
    agent none
    options { skipDefaultCheckout(false) }
    stages {
        stage('git pull') {
            agent any
            steps {
                checkout scm
            }
        }
        stage('Docker build') {
            agent any
            steps {
                sh 'docker build -t springio/gs-spring-boot-docker /var/jenkins_home/workspace/kms'
            }
        }
        stage('Docker run') {
            agent any
            steps {
                sh 'docker ps -f name=spring -q \
        | xargs --no-run-if-empty docker container stop'
//                 sh 'docker ps -f name=server -q \
//                 | xargs --no-run-if-empty docker container stop'

                sh 'docker container ls -a -f name=spring -q \
        | xargs -r docker container rm'
//                 sh 'docker container ls -a -f name=server -q \
//         | xargs -r docker container rm'

                sh 'docker images -f dangling=true && \
                docker rmi $(docker images -f dangling=true -q)'

                sh 'docker run -itd --name spring -p 443:443 springio/gs-spring-boot-docker'
//                 sh 'docker run -d --name server \
//                 -v /etc/localtime:/etc/localtime:ro \
//                 --network agaeinnet server:latest'
            }
        }
    }
}
