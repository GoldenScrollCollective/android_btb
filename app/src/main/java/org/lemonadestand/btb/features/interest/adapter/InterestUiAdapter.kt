package org.lemonadestand.btb.features.interest.adapter

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.DatePicker
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import org.lemonadestand.btb.R
import org.lemonadestand.btb.constants.ClickType
import org.lemonadestand.btb.constants.InterestFieldType
import org.lemonadestand.btb.constants.getDate
import org.lemonadestand.btb.constants.intToHexColor
import org.lemonadestand.btb.constants.isValidHexColor
import org.lemonadestand.btb.extensions.hide
import org.lemonadestand.btb.extensions.show
import org.lemonadestand.btb.features.interest.fragments.InterestsFragment.Companion.currentType
import org.lemonadestand.btb.features.interest.models.InterestModel
import org.lemonadestand.btb.interfaces.OnItemClickListener
import org.lemonadestand.btb.singleton.Singleton
import yuku.ambilwarna.AmbilWarnaDialog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class InterestUiAdapter(
	private var listValue: ArrayList<InterestModel>,
	var context: Context,
	private var listUi: ArrayList<InterestModel>
) :
	RecyclerView.Adapter<InterestUiAdapter.ViewHolder>() {

	private var onItemClick: OnItemClickListener? = null
	override fun onCreateViewHolder(
		parent: ViewGroup,
		viewType: Int
	): ViewHolder {
		val layoutInflater = LayoutInflater.from(parent.context)
		val listItem =
			layoutInflater.inflate(R.layout.row_interest_dynamic, parent, false)
		return ViewHolder(listItem)
	}

	fun setOnItemClick(onItemClickListener: OnItemClickListener) {
		this.onItemClick = onItemClickListener
	}

	@SuppressLint("SimpleDateFormat")
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val data = listUi[position]

		holder.hideAllViews()
		Log.e("type==>", data.type)

		when (data.type) {

			Singleton.InterestType.singleLine -> {
				holder.lnSingleLine.show()
				holder.tvSingleLineTitle.text = data.label
				holder.tvSingleLineValue.text = data.value
				if (data.value == null) {
					holder.tvSingleLineValue.text = ""
					holder.tvSingleLineValue.hint = data.placeholder
				}

				holder.itemView.setOnClickListener {
					currentType = InterestFieldType.AdapterClicks
					onItemClick!!.onItemClicked(data, position, ClickType.SingleLine)
				}


			}

			Singleton.InterestType.multiLine -> {
				holder.lnMultiLine.show()
				holder.tvMultiLineTitle.text = data.label
				holder.tvMultiLineValue.text = data.value

				if (data.value == null) {
					holder.tvMultiLineValue.text = ""
					holder.tvMultiLineValue.hint = data.placeholder
				}
				holder.itemView.setOnClickListener {
					currentType = InterestFieldType.AdapterClicks
					onItemClick!!.onItemClicked(data, position, ClickType.MultiLine)
				}
			}

			Singleton.InterestType.numberInput -> {
				holder.lnNumberInput.show()
				holder.tvNumberInputTitle.text = data.label
				holder.tvNumberInputValue.text = data.value
				var amount = 0.0
				if (data.value == null) {

					holder.tvNumberInputValue.text = ""
					holder.tvNumberInputValue.hint = data.placeholder
				} else {
					amount = data.value!!.toDouble()
				}



				holder.tvNumberIncrease.setOnClickListener {
					amount += 1
					data.value = amount.toString()
					holder.tvNumberInputValue.text = data.value
					currentType = InterestFieldType.AdapterClicks
					onItemClick!!.onItemClicked(data, position, ClickType.NumberInput)
				}


				holder.tvNumberDecrease.setOnClickListener {
					if (amount >= 1) {
						amount -= 1
						data.value = amount.toString()
						holder.tvNumberInputValue.text = data.value
						currentType = InterestFieldType.AdapterClicks
						onItemClick!!.onItemClicked(data, position, ClickType.NumberInput)
					}
				}

			}

			Singleton.InterestType.radio -> {
				holder.lnRadio.show()
				holder.tvRadioTitle.text = data.label
				holder.tvRadioValue.text = data.value
				if (data.value == null) {
					holder.tvRadioValue.text = ""
					holder.tvRadioValue.hint = data.placeholder
				}

				holder.itemView.setOnClickListener {
					currentType = InterestFieldType.AdapterClicks
					onItemClick!!.onItemClicked(data, position, ClickType.Radio)
				}

			}

			Singleton.InterestType.checkBox -> {
				holder.lnCheckBox.show()
				holder.tvCheckTitle.text = data.label
				holder.tvCheckValue.text = data.value

				if (data.value == null) {
					holder.tvCheckValue.text = ""
					holder.tvCheckValue.hint = data.placeholder
				}

				holder.itemView.setOnClickListener {
					currentType = InterestFieldType.AdapterClicks
					onItemClick!!.onItemClicked(data, position, ClickType.Checkbox)
				}


			}

			Singleton.InterestType.dropdownSelect -> {

				holder.lnDropdownSelect.show()
				holder.tvDropDownTitle.text = data.label
				Log.i("dropdowndata=>", data.toString())
				for (i in 0 until data.options.size) {
					if (data.options[i].value == data.value) {
						holder.tvDropDownValue.text = data.options[i].label
						break
					}
				}

				if (data.value == null) {
					holder.tvDropDownValue.text = ""
					holder.tvDropDownValue.hint = data.placeholder
				}
				holder.itemView.setOnClickListener {
					currentType = InterestFieldType.AdapterClicks
					onItemClick!!.onItemClicked(data, position, ClickType.DropdownSelect)
				}

			}

			Singleton.InterestType.datePicker -> {
				var calendar = Calendar.getInstance()
				holder.lnDatePicker.show()
				holder.tvDateTitle.text = data.label
				if (data.value != null) {
					holder.tvDateValue.text = getDate(data.value!!)
					calendar = Calendar.getInstance()
					val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

					calendar.time = sdf.parse(data.value!!)!! // all done

				}


				if (data.value == null) {
					holder.tvDateValue.text = ""
					holder.tvDateValue.hint = data.placeholder
				}
				holder.itemView.setOnClickListener {

					val year = calendar[Calendar.YEAR]
					val month = calendar[Calendar.MONTH]
					val day = calendar[Calendar.DAY_OF_MONTH]
					val datePickerDialog = DatePickerDialog(
						context,
						{ _: DatePicker?, year1: Int, month1: Int, dayOfMonth: Int ->
							calendar[Calendar.YEAR] = year1
							calendar[Calendar.MONTH] = month1
							calendar[Calendar.DAY_OF_MONTH] = dayOfMonth
							val dateFormat = SimpleDateFormat("EEE, MMM dd, yyyy", Locale.US)
							val formattedDate = dateFormat.format(calendar.time)
							holder.tvDateValue.text = formattedDate
							val dateFormat1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
							val formattedDate1 = dateFormat1.format(calendar.time)

							data.value = formattedDate1
							currentType = InterestFieldType.AdapterClicks
							onItemClick!!.onItemClicked(data, position, ClickType.DatePicker)

						}, year, month, day
					)
					datePickerDialog.show()
				}
			}

			Singleton.InterestType.colorPicker -> {
				holder.lnColorPicker.show()
				holder.tvColorTitle.text = data.label


				if (data.value != null) {
					if (isValidHexColor(data.value!!)) {
						Log.e("colorsss=>", data.value + "")
						holder.tvColorValue.setCardBackgroundColor(Color.parseColor(data.value))
					}

				}



				holder.tvColorValue.setOnClickListener {
					var currentColor = Color.parseColor("#4149b6")
					if (data.value != null) {
						currentColor = Color.parseColor(data.value)
					}

					AmbilWarnaDialog(context, currentColor, true, object : AmbilWarnaDialog.OnAmbilWarnaListener {
						override fun onOk(ambilWarnaDialog: AmbilWarnaDialog, i: Int) {
							holder.tvColorValue.setCardBackgroundColor(i)
							data.value = intToHexColor(i)
							currentType = InterestFieldType.AdapterClicks
							onItemClick!!.onItemClicked(data, position, ClickType.ColorPicker)


						}

						override fun onCancel(ambilWarnaDialog: AmbilWarnaDialog) {
							Log.e("TAG", "onCancel: ")
						}
					}).show()


				}


			}

			Singleton.InterestType.phoneNumber -> {
				holder.lnPhoneNumber.show()
				holder.tvPhoneTitle.text = data.label
				holder.tvPhoneValue.setText(data.value)
				// add to input type to phone number
				holder.tvPhoneValue.inputType = InputType.TYPE_CLASS_PHONE

				if (data.value == null) {
					holder.tvPhoneValue.setText("")
					holder.tvPhoneValue.hint = "+1201-555-0123"
				}
				holder.tvPhoneValue.setOnEditorActionListener { _, actionId, _ ->
					if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_NEXT) {
						Log.e("tvPhoneValue", holder.tvPhoneValue.text.toString())
						currentType = InterestFieldType.AdapterClicks
						data.value = holder.tvPhoneValue.text.toString()
						onItemClick!!.onItemClicked(data, position, ClickType.PhoneNumber)
						true // Return true to consume the event
					} else {
						false
					}
				}

			}

			Singleton.InterestType.fileUpload -> {
				holder.lnFileUpload.show()
				holder.tvFileTitle.text = data.label
				holder.tvFileValue.text = data.label
				if (data.value == null) {

					holder.tvFileValue.text = ""
					holder.tvFileValue.hint = data.placeholder
				}
			}

			else -> {

			}
		}


	}

	override fun getItemCount(): Int {
		return listUi.size
	}

	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


		// main layouts
		var lnSingleLine: LinearLayout
		var lnMultiLine: LinearLayout
		var lnNumberInput: LinearLayout
		var lnRadio: LinearLayout
		var lnCheckBox: LinearLayout
		var lnDropdownSelect: LinearLayout
		var lnDatePicker: LinearLayout
		var lnColorPicker: LinearLayout
		var lnPhoneNumber: LinearLayout
		var lnFileUpload: LinearLayout

		// tiles place holders
		var tvSingleLineTitle: TextView
		var tvMultiLineTitle: TextView
		var tvNumberInputTitle: TextView
		var tvRadioTitle: TextView
		var tvCheckTitle: TextView
		var tvDropDownTitle: TextView
		var tvDateTitle: TextView
		var tvColorTitle: TextView
		var tvPhoneTitle: TextView
		var tvFileTitle: TextView


		// values
		var tvSingleLineValue: TextView
		var tvMultiLineValue: TextView
		var tvNumberInputValue: TextView
		var tvRadioValue: TextView
		var tvCheckValue: TextView
		var tvDropDownValue: TextView
		var tvDateValue: TextView
		var tvColorValue: CardView
		var tvPhoneValue: EditText
		var tvFileValue: TextView
		var tvNumberIncrease: TextView
		var tvNumberDecrease: TextView


		init {
			lnSingleLine = itemView.findViewById(R.id.ln_single_line)
			lnMultiLine = itemView.findViewById(R.id.ln_multi_line)
			lnNumberInput = itemView.findViewById(R.id.ln_number_input)
			lnRadio = itemView.findViewById(R.id.ln_radio)
			lnCheckBox = itemView.findViewById(R.id.ln_checkbox)
			lnDropdownSelect = itemView.findViewById(R.id.ln_dropdown_select)
			lnDatePicker = itemView.findViewById(R.id.ln_date_picker)
			lnColorPicker = itemView.findViewById(R.id.ln_color_picker)
			lnPhoneNumber = itemView.findViewById(R.id.ln_phone_number)
			lnFileUpload = itemView.findViewById(R.id.ln_file_upload)

			tvSingleLineTitle = itemView.findViewById(R.id.txt_single_line_title)
			tvMultiLineTitle = itemView.findViewById(R.id.txt_multi_line_title)
			tvNumberInputTitle = itemView.findViewById(R.id.txt_number_input_title)
			tvRadioTitle = itemView.findViewById(R.id.txt_radio_title)
			tvCheckTitle = itemView.findViewById(R.id.txt_check_title)
			tvDropDownTitle = itemView.findViewById(R.id.txt_dropdown_title)
			tvDateTitle = itemView.findViewById(R.id.txt_date_title)
			tvColorTitle = itemView.findViewById(R.id.txt_color_title)
			tvPhoneTitle = itemView.findViewById(R.id.txt_phone_title)
			tvFileTitle = itemView.findViewById(R.id.txt_file_title)

			tvSingleLineValue = itemView.findViewById(R.id.txt_single_line_value)
			tvNumberInputValue = itemView.findViewById(R.id.txt_number_input_value)
			tvMultiLineValue = itemView.findViewById(R.id.txt_multi_line_value)
			tvRadioValue = itemView.findViewById(R.id.txt_radio_value)
			tvCheckValue = itemView.findViewById(R.id.txt_check_value)
			tvDropDownValue = itemView.findViewById(R.id.txt_dropdown_value)
			tvDateValue = itemView.findViewById(R.id.txt_date_value)
			tvColorValue = itemView.findViewById(R.id.card_color_value)
			tvPhoneValue = itemView.findViewById(R.id.edt_phone_value)
			tvFileValue = itemView.findViewById(R.id.txt_file_value)
			tvNumberIncrease = itemView.findViewById(R.id.number_increase)
			tvNumberDecrease = itemView.findViewById(R.id.number_decrease)


		}

		fun hideAllViews() {
			lnSingleLine.hide()
			lnMultiLine.hide()
			lnNumberInput.hide()
			lnRadio.hide()
			lnCheckBox.hide()
			lnDropdownSelect.hide()
			lnDatePicker.hide()
			lnColorPicker.hide()
			lnPhoneNumber.hide()
			lnFileUpload.hide()
		}


	}

	fun updateData(list: ArrayList<InterestModel>) {
		this.listUi = list
		notifyDataSetChanged()
	}
}