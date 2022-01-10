package exercicis

import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JComboBox
import javax.swing.JTextArea
import java.awt.BorderLayout
import java.awt.FlowLayout
import javax.swing.JPanel
import java.awt.Color
import javax.swing.JScrollPane
import java.io.FileInputStream
import com.google.firebase.FirebaseOptions
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.DocumentChange
import com.google.firebase.FirebaseApp
import com.google.firebase.cloud.FirestoreClient
import java.awt.EventQueue

class EstadisticaCF : JFrame() {

    val etCombo = JLabel("Llista de províncies:")
    val comboProv = JComboBox<String>()

    val etiqueta = JLabel("Estadístiques:")
    val area = JTextArea()

    // en iniciar posem un contenidor per als elements anteriors
    init {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        setBounds(100, 100, 450, 400)
        setLayout(BorderLayout())
        // contenidor per als elements

        val panell1 = JPanel(FlowLayout())
        panell1.add(etCombo)
        panell1.add(comboProv)
        getContentPane().add(panell1, BorderLayout.NORTH)

        val panell2 = JPanel(BorderLayout())
        panell2.add(etiqueta, BorderLayout.NORTH)
        area.setForeground(Color.blue)
        area.setEditable(false)
        val scroll = JScrollPane(area)
        panell2.add(scroll, BorderLayout.CENTER)
        getContentPane().add(panell2, BorderLayout.CENTER)

        setVisible(true)

        val serviceAccount = FileInputStream("xat-ad-firebase-adminsdk-my2d0-8c69944b34.json")

        val options = FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build()

        FirebaseApp.initializeApp(options)

        val database = FirestoreClient.getFirestore()

        //Instruccions per a omplir el JComboBox amb les províncies
        //Deben ser estas las instrucciones? No, aqui:
        //https://console.firebase.google.com/u/1/project/xat-ad/firestore/data/~2FEstadistica~2F02CSKIYYWcUgmiELlx0C
        database.collection("Xats").document("XatProva").collection("missatges").addSnapshotListener { snapshots, e ->
            if (e != null) {
                System.err.println("Listen failed: " + e)
                return@addSnapshotListener
            }

            for (dc in snapshots!!.getDocumentChanges()) {
                when (dc.getType()) {
                    DocumentChange.Type.ADDED ->
                        area.append(dc.getDocument().getString("nom") + ": " + dc.getDocument().getString("contingut") + "\n")

                    DocumentChange.Type.MODIFIED ->
                        println("Missatge modificat: " + dc.getDocument().getData());

                    DocumentChange.Type.REMOVED ->
                        println("Missatge esborrat: " + dc.getDocument().getData());
                }
            }
        }

        // Instruccions per agafar la informació de tots els anys de la província triada
        comboProv.addActionListener() {

        }
    }
}

fun main(args: Array<String>) {
    EventQueue.invokeLater {
        EstadisticaCF().isVisible = true
    }
}
