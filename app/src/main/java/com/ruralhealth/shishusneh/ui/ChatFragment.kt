package com.ruralhealth.shishusneh.ui

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ruralhealth.shishusneh.R
import com.ruralhealth.shishusneh.databinding.FragmentChatBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChatFragment : Fragment() {
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        addMessage("Hello! I am your AI Baby Assistant. How can I help you and your little one today?", isBot = true)

        binding.btnSend.setOnClickListener {
            val query = binding.etMessage.text.toString().trim()
            if (query.isNotEmpty()) {
                addMessage(query, isBot = false)
                binding.etMessage.text?.clear()
                simulateResponse(query)
            }
        }
    }

    private fun addMessage(text: String, isBot: Boolean) {
        val textView = TextView(requireContext()).apply {
            this.text = text
            setPadding(40, 30, 40, 30)
            textSize = 15f
            setTextColor(ContextCompat.getColor(context, R.color.text_primary))
            background = ContextCompat.getDrawable(
                context, 
                if (isBot) R.drawable.bg_chat_bubble_bot else R.drawable.bg_chat_bubble_user
            )
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = if (isBot) Gravity.START else Gravity.END
                setMargins(
                    if (isBot) 0 else 100, 
                    12, 
                    if (isBot) 100 else 0, 
                    12
                )
            }
        }
        
        binding.llChatContainer.addView(textView)
        binding.svChat.post {
            binding.svChat.fullScroll(View.FOCUS_DOWN)
        }
    }

    private fun simulateResponse(query: String) {
        lifecycleScope.launch {
            delay(1200)
            val lower = query.lowercase()
            val reply = when {
                lower.contains("fever") || lower.contains("hot") || lower.contains("temperature") -> 
                    "If your baby feels warm, check their temperature. Keep them hydrated and dress them in light cotton clothes. If the fever is high or they are very lethargic, please visit a doctor immediately."
                
                lower.contains("feed") || lower.contains("milk") || lower.contains("breastfeed") || lower.contains("hungry") -> 
                    "For the first 6 months, exclusive breastfeeding is the gold standard. Feed on demand. After 6 months, you can start small portions of mashed banana, dal water, or khichdi alongside breast milk."
                
                lower.contains("cry") || lower.contains("colic") || lower.contains("upset") -> 
                    "Babies often cry when they are hungry, have a wet diaper, feel too hot/cold, or are just tired. If they cry excessively, try a gentle tummy massage or 'burping' them, as it might be gas (colic)."
                
                lower.contains("vaccine") || lower.contains("injection") || lower.contains("polio") || lower.contains("bcg") -> 
                    "Vaccinations protect your baby from life-threatening diseases. You can see your baby's personalized schedule in the 'Vaccine' tab. Don't worry if there's a slight fever after a shot; it's normal!"
                
                lower.contains("sleep") || lower.contains("night") || lower.contains("nap") -> 
                    "Newborns sleep about 14-17 hours a day. Establish a calm routine—dim the lights and keep the room quiet. Always place your baby on their back to sleep on a firm surface."
                
                lower.contains("bath") || lower.contains("clean") || lower.contains("hygiene") || lower.contains("soap") -> 
                    "A warm bath 2-3 times a week is usually enough for a newborn. Use a mild, pH-neutral baby soap. Keep the umbilical cord area dry until it falls off naturally."
                
                lower.contains("weight") || lower.contains("growth") || lower.contains("height") || lower.contains("small") -> 
                    "Every baby grows at their own pace. Use our 'Growth' tab to record weight and height. If your baby is active, feeding well, and passing urine 6-8 times a day, they are likely doing great!"
                
                lower.contains("milestone") || lower.contains("walk") || lower.contains("sit") || lower.contains("talk") || lower.contains("smile") -> 
                    "Milestones are exciting! Most babies smile by 2 months and sit by 6 months. Check our 'Milestone' tab to see what to expect next. Every baby has their own timeline!"
                
                lower.contains("teeth") || lower.contains("bite") || lower.contains("chew") -> 
                    "Teething usually starts around 6 months. Your baby might drool more or want to chew on things. A clean, chilled (not frozen) teething ring can provide great relief."
                
                lower.contains("poop") || lower.contains("potty") || lower.contains("constipation") || lower.contains("diarrhea") -> 
                    "Breastfed babies can have very frequent or very rare bowel movements—both can be normal! If the poop is very hard (constipation) or very watery (diarrhea) for more than a day, consult your health worker."

                lower.contains("hello") || lower.contains("hi") || lower.contains("hey") -> 
                    "Hello Ma! I am here to help you track and care for your baby. You can ask me about feeding, sleep, vaccinations, or anything else on your mind."

                else -> "That is an important question. I am constantly learning to better assist mothers like you. For any specific medical concerns, please speak with your local ASHA worker or pediatrician."
            }
            addMessage(reply, isBot = true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
