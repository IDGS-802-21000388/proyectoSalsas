import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.ProduccionAdapter
import com.example.proyecto.R
import com.example.proyecto.RecipeFragment
import com.example.proyecto.apiservice.AuthApiService
import com.example.proyecto.apiservice.RetrofitClient
import com.example.proyecto.models.Pedido
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProduccionFragment : Fragment() {

    private lateinit var pedidosRecyclerView: RecyclerView
    private lateinit var produccionAdapter: ProduccionAdapter
    private val apiService: AuthApiService by lazy {
        RetrofitClient.instance  // Utilizando la instancia de RetrofitClient
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_produccion, container, false)
        // Recuperar el idUsuario de las SharedPreferences
        val sharedPref = requireContext().getSharedPreferences("miAppPref", Context.MODE_PRIVATE)
        val idUsuario = sharedPref.getInt("idUsuario", -1)

        pedidosRecyclerView = view.findViewById(R.id.pedidosRecyclerView)
        pedidosRecyclerView.layoutManager = LinearLayoutManager(context)

        // Instanciar el adaptador pasando una lista vacía y la función de clic
        produccionAdapter = ProduccionAdapter(requireContext(), emptyList()) { pedido ->
            // Acciones al hacer clic en un pedido
            val fragment = RecipeFragment()
            val args = Bundle()
            args.putParcelable("pedido", pedido)
            fragment.arguments = args

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        // Asignar el adaptador al RecyclerView
        pedidosRecyclerView.adapter = produccionAdapter
        // Hacer la solicitud a la API
        obtenerPedidosDesdeAPI(idUsuario = idUsuario)

        return view
    }

    private fun obtenerPedidosDesdeAPI(idUsuario: Int) {
        apiService.obtenerPedidos(idUsuario).enqueue(object : Callback<List<Pedido>> {
            override fun onResponse(call: Call<List<Pedido>>, response: Response<List<Pedido>>) {
                if (response.isSuccessful) {
                    val pedidos = response.body()
                    if (pedidos != null) {
                        produccionAdapter.setPedidos(pedidos)
                    }
                } else {
                    // Manejar el caso de respuesta no exitosa
                    Toast.makeText(context, "Error al obtener los pedidos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Pedido>>, t: Throwable) {
                // Manejar errores de red u otros problemas
                Toast.makeText(context, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
