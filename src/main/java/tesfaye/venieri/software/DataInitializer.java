package tesfaye.venieri.software;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tesfaye.venieri.software.user.User;
import tesfaye.venieri.software.user.UserRepository;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(StoryRepository storyRepository, ChoiceRepository choiceRepository, UserRepository userRepository) {
        return args -> {
            // Crea un utente di esempio
            User user = new User("admin", "password", "admin@example.com");
            userRepository.save(user);
            
            // Crea alcune storie di esempio
            Story story1 = new Story(
                "L'inizio dell'avventura", 
                "Ti svegli in una stanza sconosciuta. La luce filtra attraverso una piccola finestra. Vedi una porta chiusa e uno strano oggetto sul tavolo.", 
                false
            );
            
            Story story2 = new Story(
                "La porta misteriosa", 
                "Apri la porta e ti ritrovi in un corridoio buio. Senti dei rumori provenire da entrambe le direzioni.", 
                false
            );
            
            Story story3 = new Story(
                "L'oggetto misterioso", 
                "Prendi l'oggetto dal tavolo. È una chiave dorata con strani simboli incisi. Improvvisamente, senti un rumore provenire da dietro la porta.", 
                false
            );
            
            Story story4 = new Story(
                "Il corridoio a sinistra", 
                "Decidi di andare a sinistra. Il corridoio diventa sempre più stretto e buio. In fondo vedi una luce fioca.", 
                false
            );
            
            Story story5 = new Story(
                "Il corridoio a destra", 
                "Decidi di andare a destra. Il corridoio si allarga e vedi diverse porte lungo le pareti.", 
                false
            );
            
            Story story6 = new Story(
                "La fuga", 
                "Riesci a trovare l'uscita e scappi dal misterioso edificio. Sei libero, ma hai la sensazione che questa avventura non sia finita...", 
                true
            );
            
            Story story7 = new Story(
                "Intrappolato", 
                "La porta si chiude alle tue spalle e non riesci più ad aprirla. Sei intrappolato in questa stanza misteriosa, forse per sempre.", 
                true
            );
            
            // Salva le storie
            storyRepository.save(story1);
            storyRepository.save(story2);
            storyRepository.save(story3);
            storyRepository.save(story4);
            storyRepository.save(story5);
            storyRepository.save(story6);
            storyRepository.save(story7);
            
            // Crea le scelte
            Choice choice1 = new Choice("Apri la porta", story2);
            Choice choice2 = new Choice("Esamina l'oggetto sul tavolo", story3);
            
            Choice choice3 = new Choice("Vai a sinistra", story4);
            Choice choice4 = new Choice("Vai a destra", story5);
            
            Choice choice5 = new Choice("Usa la chiave sulla porta", story2);
            Choice choice6 = new Choice("Nascondi la chiave e aspetta", story7);
            
            Choice choice7 = new Choice("Segui la luce", story6);
            Choice choice8 = new Choice("Torna indietro", story2);
            
            Choice choice9 = new Choice("Prova ad aprire una delle porte", story7);
            Choice choice10 = new Choice("Continua fino in fondo al corridoio", story6);
            
            // Collega le scelte alle storie
            choice1.setCurrentStory(story1);
            choice2.setCurrentStory(story1);
            
            choice3.setCurrentStory(story2);
            choice4.setCurrentStory(story2);
            
            choice5.setCurrentStory(story3);
            choice6.setCurrentStory(story3);
            
            choice7.setCurrentStory(story4);
            choice8.setCurrentStory(story4);
            
            choice9.setCurrentStory(story5);
            choice10.setCurrentStory(story5);
            
            // Salva le scelte
            choiceRepository.save(choice1);
            choiceRepository.save(choice2);
            choiceRepository.save(choice3);
            choiceRepository.save(choice4);
            choiceRepository.save(choice5);
            choiceRepository.save(choice6);
            choiceRepository.save(choice7);
            choiceRepository.save(choice8);
            choiceRepository.save(choice9);
            choiceRepository.save(choice10);
            
            System.out.println("Database inizializzato con dati di esempio!");
        };
    }
}