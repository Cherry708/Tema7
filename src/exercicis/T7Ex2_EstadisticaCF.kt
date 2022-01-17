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
    val cmbProvincia = JComboBox<String>()

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
        panell1.add(cmbProvincia)
        getContentPane().add(panell1, BorderLayout.NORTH)

        val panell2 = JPanel(BorderLayout())
        panell2.add(etiqueta, BorderLayout.NORTH)
        area.setForeground(Color.blue)
        area.setEditable(false)
        val scroll = JScrollPane(area)
        panell2.add(scroll, BorderLayout.CENTER)
        getContentPane().add(panell2, BorderLayout.CENTER)

        setVisible(true)

        //Referencias
        val serviceAccount = FileInputStream("xat-ad-firebase-adminsdk-my2d0-8c69944b34.json")

        val options = FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build()

        FirebaseApp.initializeApp(options)

        val db = FirestoreClient.getFirestore()

        // Instruccions per a omplir el JComboBox amb les províncies
        val conjuntoProvincias = mutableSetOf<String>()

        db.collection("Estadistica").orderBy("Provincia").addSnapshotListener { snapshots, e ->
            for (dc in snapshots!!.documents) {
                conjuntoProvincias.add(
                    dc.getString("Provincia") as String
                )
            }

            for (provincia in conjuntoProvincias) {
                cmbProvincia.addItem(provincia)
            }
        }


        // Instruccions per agafar la informació de tots els anys de la província triada
        cmbProvincia.addActionListener() {
            area.text = ""

            db.collection("Estadistica").orderBy("any").addSnapshotListener { snapshots, e ->
                for (dc in snapshots!!.documents) {
                    if (dc.getString("Provincia") == cmbProvincia.selectedItem) {
                        val any = dc.getString("any") as String
                        val dones = dc.getString("Dones") as String
                        val homes = dc.getString("Homes") as String
                        area.text += ("$any: $dones - $homes \n")
                    }

                }
            }
        }
    }
}

fun main(args: Array<String>) {
    EventQueue.invokeLater {
        EstadisticaCF().isVisible = true
    }
}

