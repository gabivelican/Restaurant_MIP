Proiect Meniu Restaurant — Explicații pentru prezentare (în română, simplu)

Scop: Acest README explică structura proiectului, rolul fiecărei clase și pașii pentru rulare și urcare pe GitHub. Este scris foarte clar, pas cu pas, astfel încât să poți explica profesorului orice parte.

1) Unde este proiectul (calea pe calculator):
- Directorul proiectului: D:\Facultate\ANUL 2\SEM 1\MIP\Tema 1
- Aici se găsesc subdirectoarele: `src/main/java` (cod sursă), `src/main/resources` (resurse), `target` (artefacte generate) și `pom.xml` (configurația Maven).

2) Cum pornește aplicația (comenzi în cmd.exe pe Windows):
- Mergi în directorul proiectului (important: comanda `mvn` trebuie rulată din folderul unde este `pom.xml`):
  cd /d "D:\Facultate\ANUL 2\SEM 1\MIP\Tema 1"
- Compilează pachetul cu Maven (sări peste testare locală):
  mvn -DskipTests package
- După ce pachetul este creat, pornește jar-ul:
  java -jar target\restaurant-1.0-SNAPSHOT.jar

Observație: eroarea "The goal you specified requires a project to execute but there is no POM in this directory" apare dacă rulezi `mvn` din alt folder (de exemplu C:\Users\Gabriel). Asigură-te că ești în directorul proiectului înainte de a rula `mvn`.

3) Cum urci proiectul pe GitHub (comenzi cmd.exe):
- Dacă nu ai încă un repo local inițializat, fă:
  cd /d "D:\Facultate\ANUL 2\SEM 1\MIP\Tema 1"
  git init
  git add .
  git commit -m "Initial commit Iteration A"
  git branch -M main
  git remote add origin https://github.com/gabivelican/Restaurant_MIP.git
  git push -u origin main

- Dacă ai deja un repo local și trebuie doar să setezi remote:
  git remote add origin https://github.com/gabivelican/Restaurant_MIP.git
  git push -u origin main

4) Structura și ce face fiecare clasă (explicații foarte simple):

- `Product` (clasa de bază)
  - Ce este: Reprezintă orice produs din meniu.
  - Atribute principale: `name` (numele produsului), `price` (prețul).
  - De ce există: multe produse (mâncare și băuturi) au aceleași informații; le punem aici ca să nu le scriem de două ori.
  - Metode importante: `getName()`, `getPrice()`, `toString()` (o descriere textuală de bază).

- `Food` (mâncare) — extinde `Product`
  - Ce este: produse care sunt mâncare.
  - Atribut specific: `weight` (gramaj) și `vegetarian` (boolean) — dacă e vegetarian.
  - `toString()` este suprascris astfel încât să afişeze și gramajul.
  - Exemplu: `new Food("Pizza Carbonara", 52.5, 400, false)` înseamnă o pizza cu nume, preț 52.5 RON, 400g, nu vegetarian.

- `Drink` (băutură) — extinde `Product`
  - Ce este: produse care sunt băuturi.
  - Atribute specifice: `volume` (ml) și `alcoholic` (boolean) — dacă conține alcool.
  - `toString()` suprascris pentru a afișa volumul și dacă e alcoolică.
  - Exemplu: `new Drink("Limonada", 15.0, 400, false, true)` — limonadă, 400ml, nu alcoolică (boolean poate varia în cod).

- `Category` (enum)
  - Ce este: un set de categorii (EX: MAIN_COURSE, DESSERT, SOFT_DRINK, ALCOHOLIC_DRINK etc.).
  - De ce: pentru a organiza meniul pe categorii (cerință din iterația 3).

- `Menu` (gestionează produsele)
  - Ce este: clasa care ține o colecție de produse, grupate pe categorii.
  - Funcții importante:
    - `addProduct(Product p, Category c)` — adaugă un produs într-o categorie.
    - `printMenu(String restaurantName)` — afișează tot meniul în consolă (sau `printMenuIteration1` folosit pentru formatul simplu din iteratia 1).
    - `getVegetarianSorted()` — întoarce produsele vegetariene sortate alfabetic.
    - `getAveragePrice(Category c)` — calculează prețul mediu pentru o categorie (ex: deserturi).
    - `existsProductMoreExpensiveThan(double price)` — verifică dacă există produse mai scumpe decât o valoare.
    - `findProductByName(String name)` — caută un produs după nume și returnează un `Optional<Product>` (pentru a nu da eroare dacă nu există).
  - De ce: pentru a îndeplini cerințele de organizare, filtrare și căutare.

- `Order` (comandă)
  - Ce este: clasa care ține produsele comandate și cantitățile lor.
  - Structură internă: un `Map<Product, Integer>` (produs->cantitate).
  - Funcții importante:
    - `addProduct(Product p, int qty)` — adaugă o linie la comandă.
    - `calculateTotalWithVAT()` — calculează totalul și aplică TVA (valoarea TVA este stocată static în clasă).
    - `calculateTotalWithDiscount(DiscountRule rule)` — calculează total aplicând o regulă de discount (dacă există).
  - De ce: să poți simula o comandă și să vezi cum se aplică TVA și oferte.

- `DiscountRule` (interfață funcțională)
  - Ce este: o interfață folosită pentru a defini reguli de discount dinamice (Strategy pattern simplu).
  - Metodă principală: `apply(double totalWithVAT, Map<Product,Integer> items)` — returnează totalul după aplicarea discountului.
  - De ce: pentru a permite reguli variabile (HappyHour, Valentine’s discount etc.) fără a rescrie `Order`.

- `Pizza` + `Pizza.Builder` (pattern Builder)
  - Ce este: o clasă specială pentru pizza, care permite construcția unei pizza custom (blat, sos, topping-uri opționale).
  - De ce: cerință pentru iterația 3. Builder-ul impune setarea câmpurilor obligatorii (ex: tip blat și sos) și acceptă topping-uri opționale.
  - Cum folosești Builder-ul:
    Pizza p = new Pizza.Builder("Pizza Custom", 60.0, "thin", "tomato")
                 .addTopping("mozzarella")
                 .addTopping("mushrooms")
                 .build();

- `AppConfig` și `ConfigLoader`
  - `AppConfig`: model pentru configurație (nume restaurant, TVA etc.).
  - `ConfigLoader`: încarcă `config.json` (din `src/main/resources`) folosind Gson și returnează un `AppConfig`.
  - Ce se întâmplă dacă lipsește fișierul sau e corupt: `ConfigLoader` ar trebui să prindă excepțiile (FileNotFoundException, JsonSyntaxException) și să afișeze mesaje clare ca să aplicația nu „crape”.

- `MenuExporter`
  - Ce face: serializează meniul curent în JSON și îl scrie într-un fișier (`menu_export.json`).
  - De ce: pentru a avea un export ușor de partajat (cerința iteratiei 4 și 6).

- `Main` (doar unul)
  - Ce face: în acest proiect `Main.java` este un singur punct de pornire care demonstrează toate cele 4 iterații, în ordine:
    1. Construiește un meniu simplu și îl afișează (iteratia 1).
    2. Creează o comandă, adaugă produse și calculează totalul cu TVA și cu o regulă de discount (iteratia 2).
    3. Demonstrează funcții de meniu avansate (filtre, medii, căutare) și folosește builder-ul de Pizza (iteratia 3).
    4. Încarcă configurația din `config.json`, afișează numele restaurantului și TVA, afișează meniul și exportă JSON (iteratia 4).
  - De ce liniile ca `Food pizza = new Food("Pizza Carbonara", 52.5, 400, false);` apar în `main`:
    - Sunt exemple "hardcodate" care arată cum se creează obiecte concrete.
    - În laborator e acceptat să ai date hardcodate pentru a demonstra funcționalitatea.
    - Profesorului îi poți spune: "Aici construiesc obiectul food cu nume, preț, gramaj și flag vegetarian; îl adaug în comandă/meniu ca să testez calculul." 

5) De ce am făcut un singur `Main` (nu 4 Main-uri separate):
- Simplitate: un singur punct de intrare e mai clar pentru profesor.
- Demonstrează progresul: rulând `main` se văd toate iterațiile în ordine, ceea ce arată evoluția proiectului.

6) Ce trebuie să explici profesorului despre un exemplu concret (ce ÎNTREBI s-ar putea):
- "De ce ai folosit o clasă Product?" — pentru a evita duplicarea codului; nume și preț sunt comune.
- "Cum se aplică TVA?" — există un câmp static în `Order` (9% default); `calculateTotalWithVAT` calculează subtotalul + TVA.
- "Cum funcționează o ofertă (discount)?" — `DiscountRule` e o funcționalitate pe care o injectăm în `Order#calculateTotalWithDiscount`. Astfel `Order` nu știe detalii despre regulă și rămâne simplu.
- "De ce Optional la căutare?" — dacă nu găsim produsul, returnăm `Optional.empty()` în loc să aruncăm excepție; e mai sigur.

7) Pași pentru a verifica și rezolva erorile (dacă primești mesaje):
- Dacă `mvn package` eșuează cu mesajul că nu găsește POM, asigură-te că ești în folderul corect (vezi pasul 2).
- Dacă compilarea Java arată erori, rulează `mvn -DskipTests package -e` pentru mai multe detalii și uită-te la clasele menționate în mesaj.
- Dacă `config.json` lipsește sau e corupt, `Main` verifică dacă `ConfigLoader.load(...)` returnează null și oprește aplicația controlat; explică asta la prezentare.

8) Ce poți demonstra în fața profesorului (scenariu pas-cu-pas):
- Deschizi proiectul în IDE (IntelliJ). Arăți `Main.java` (un singur main). Spui: "Aici rulez toate cele 4 iterații." Apasă Run.
- În consolă se vor afișa secțiunile: Iteration 1, Iteration 2, Iteration 3, Iteration 4.
- Oprește-te la fiecare secțiune și explică: ce creez aici, de ce adaug în meniu/comandă, cum se calculează totalul.
- Arată fișierul `src/main/resources/config.json` și explică cum se încarcă configurarea (nume restaurant, TVA).
- Arată `menu_export.json` generat (după ce rulezi exportul) pentru a demonstra funcția de export.
- Dacă profesor întreabă despre coduri mai avansate (Stream/API, Optional, Builder), explică pe scurt cu exemple din `Menu`, `Order`, `Pizza`.

9) Elemente de îmbunătățit (următorii pași):
- Integrare GUI (JavaFX) — cerința iterației 5: se poate face o aplicație cu ListView + formular detalii.
- Persistență în DB (PostgreSQL + JPA) — cerința iterației 6.
- Concurență și UI responsiveness — iterația 8.

10) Întrebări frecvente profesor-student (răspunsuri scurte pe care le poți memoriza):
- "Unde pui TVA-ul?" — În `Order` există un câmp static (de exemplu `VAT = 0.09`).
- "De ce ai folosit Gson?" — pentru a citi/scrie JSON ușor (config și export).
- "Cum adaug o ofertă nouă?" — implementezi o nouă clasă/lamdba care respectă `DiscountRule` și o injectezi la calcul.

11) Explicație foarte simplă (pentru un copil de clasa a 2-a)

- Gândește-te la aplicație ca la o carte de bucate digitală:
  - Un "Product" este ca un joc de jucărie: are un nume și un preț.
  - "Food" (mâncare) este un produs care are în plus greutatea (cât cântărește).
  - "Drink" (băutură) este un produs care are în plus volumul (câtă băutură este).

- "Menu" este ca o cutie cu rafturi: fiecare raft (categorie) ține produse.
  - Când vrei ceva din meniu, iei cutia, cauți raftul și iei produsul.

- "Order" (comanda) este ca o listă de cumpărături: pui ce vrei și câte bucăți.
  - La final, calculăm cât costă totul și adăugăm TVA (o mică taxă).

- "Pizza.Builder" este ca un atelier unde construiești pizza pas cu pas: pui blatul, pui sosul, apoi adaugi topping-uri.

Ce spui profesorului când îi arăți aplicația (foarte pe scurt):
- "Am un singur `Main`. Când rulez, se văd cele 4 etape cerute: meniu simplu, comandă + TVA, funcții avansate și citirea configurării din JSON."
- "Folosesc `Product` ca să nu repet codul; `Food` și `Drink` extind `Product`."
- "Căutarea returnează `Optional` ca să nu arunce eroare dacă nu găsim nimic."
